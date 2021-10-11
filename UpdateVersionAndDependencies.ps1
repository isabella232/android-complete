param (
 [Parameter(Mandatory= $true)] [String] $Lib,
 [Parameter(Mandatory= $false)] [String] $VersionType,
 [Parameter(Mandatory= $false)] [String] $CommonDependencyVersion,
 [Parameter(Mandatory= $false)] [String] $Common4jDependencyVersion,
 [Parameter(Mandatory= $false)] [String] $Broker4jDependencyVersion,
 [Parameter(Mandatory= $false)] [String] $CommonSubmoduleBranch
)

Function Get-VersionFilePath {
param (
[Parameter()] [String] $LibName
)
    switch($LibName.ToLower()){
        "common" { return "$PSScriptRoot\$LibName\versioning\version.properties" }
        "common4j" { return "$PSScriptRoot\common\$LibName\versioning\version.properties" }
        "broker4j" { return "$PSScriptRoot\broker\$LibName\versioning\version.properties" } 
        "broker" { return "$PSScriptRoot\$LibName\AADAuthenticator\versioning\version.properties" } 
        "msal" {return "$PSScriptRoot\$LibName\$LibName\versioning\version.properties"}
        "adal" {return "$PSScriptRoot\$LibName\$LibName\versioning\version.properties"}
        Default {
            Write-Error "invalid LibName, supported values are {adal, msal, common, common4j, broker4j, broker}"
            exit
        }
    }  
}

Function Get-GradleFilePath {
param (
[Parameter()] [String] $LibName
)
    switch($LibName.ToLower()){
        "common4j" { return "$PSScriptRoot\common\$LibName\build.gradle" } 
        "broker4j" { return "$PSScriptRoot\broker\$LibName\build.gradle" } 
        "broker" { return "$PSScriptRoot\$LibName\AADAuthenticator\build.gradle" } 
        Default {return "$PSScriptRoot\$LibName\$LibName\build.gradle"}
    }
}

Function Update-Version {
param(
[Parameter()] [String] $LibName,
[Parameter()] [String] $VersionType
)
    $versionFilePath = Get-VersionFilePath $LibName
    $Properties = Get-Content -Path $versionFilePath -Raw | ConvertFrom-StringData
    $currentVersion = [Version]$Properties["versionName"].ToString().Replace("-RC", ".")
    Write-Output "Current Version of $LibName is $($Properties.versionName)"

    switch($VersionType.ToLower()) {

        "major" {
            $newVersion = [version]::new($currentVersion.Major +1, $currentVersion.Minor, $currentVersion.Build)
        }

        "minor" {
            $newVersion = [version]::new($currentVersion.Major, $currentVersion.Minor + 1, $currentVersion.Build)
        }

        "patch" {
            $newVersion = [version]::new($currentVersion.Major, $currentVersion.Minor, $currentVersion.Build + 1)
        }

        "rc" {
            $newVersion = [version]::new($currentVersion.Major, $currentVersion.Minor, $currentVersion.Build, $currentVersion.Revision + 1)
        }

        Default { 
            Write-Error "Invalid VersionType, supported values are {major, minor, patch, rc}"
            exit
        }
    
    }

    $oldVersionName = $Properties.versionName
    if($newVersion.Revision -gt 0) {
        $newVersionName = "$($newVersion.Major).$($newVersion.Minor).$($newVersion.Build)-RC$($newVersion.Revision)"
    } else {
        $newVersionName = "$($newVersion.Major).$($newVersion.Minor).$($newVersion.Build)"
    }

    Write-Output "Updating version number for $LibName to $newVersionName"

    (Get-Content -Path $versionFilePath) -replace $Properties.versionName, $newVersionName | Set-Content -Path $versionFilePath

    Write-Output "Version updated!"

}

Function Update-Dependency {
param(
[Parameter()] [String] $LibName,
[Parameter()] [String] $Dependency,
[Parameter()] [String] $DependencyVersion
)
    $buildGradlePath = Get-GradleFilePath $LibName
    Write-Output "Updating $Dependency Dependency for $LibName to $DependencyVersion"
    (Get-Content -Path $buildGradlePath) `
        -replace "com.microsoft.identity:${Dependency}:[\d+\.]+[-RC\d+]*", "com.microsoft.identity:${Dependency}:$DependencyVersion" `
        -replace "name: '$Dependency', version: '[\d+\.]+[-RC\d+]*'", "name: '$Dependency', version: '$DependencyVersion'" `
        | Set-Content -Path $buildGradlePath
}

Function Update-CommonSubmodule {
param(
[Parameter()] [String] $LibName,
[Parameter()] [String] $BranchName
)
    Set-Location $PSScriptRoot\$LibName\common\
    git checkout $BranchName
    git pull
    Set-Location $PSScriptRoot
}

if($Lib -notin "adal", "msal", "broker", "broker4j", "common", "common4j") {
    Write-Error "Invalid Lib Name $Lib"
    exit
}

if($VersionType -ne "") {
    Write-Host "Updating $VersionType versi for $Lib" -ForegroundColor Cyan
    Update-Version $Lib $VersionType
}

if ($CommonDependencyVersion -ne "" -and $Lib -in "msal", "adal", "broker" ) {
    Write-Host "Updating common dep for $Lib" -ForegroundColor Cyan
    Update-Dependency $Lib "common" $CommonDependencyVersion
}

if ($Common4jDependencyVersion -ne "" -and $Lib -in "broker4j", "common") {
    Write-Host "Updating common4j dep for $Lib" -ForegroundColor Cyan
    Update-Dependency $Lib "common4j" $Common4jDependencyVersion
}

if ($Broker4jDependencyVersion -ne "" -and $Lib -eq "broker") {
    Write-Host "Updating broker4j dep for $Lib" -ForegroundColor Cyan
    Update-Dependency $Lib "broker4j" $Broker4jDependencyVersion
}

if ($CommonSubmoduleBranch -ne "" -and $Lib -in "msal", "adal", "broker") {
    Write-Host "Updating common submodule dep for $Lib" -ForegroundColor Cyan
    Update-CommonSubmodule $Lib $CommonSubmoduleBranch
}

Write-Host "All Done!" -ForegroundColor Green