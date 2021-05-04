param ($gcloud_project_id)
gcloud -v

# Uninstall gclooud

$sdkRoot = gcloud info --format='value(installation.sdk_root)'
$sdkConfig = gcloud info --format='value(config.paths.global_config_dir)'

$sdkRoot
$sdkConfig

Remove-Item $sdkRoot -Recurse -Force
Remove-Item $sdkConfig -Recurse -Force

# Do a fresh install of gcloud

$baseDir = "$HOME\gcloud"
$gcloudDir = "$baseDir\gcloud-extracted\google-cloud-sdk\bin"
$gcloud_version = "320.0.0"

if (!(Test-Path $gcloudDir\gcloud -PathType Leaf)) {
    Write-Output "gcloud not found at $gcloudDir"

    Write-Output "Downloading gcloud sdk with version $gcloud_version"

    $downloadLink = "https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-$gcloud_version-windows-x86-bundled-python.zip"

    Write-Output "Using download link: $downloadLink"

    $dest = "$env:Temp\GoogleCloudSDKInstaller.zip"
    (New-Object Net.WebClient).DownloadFile($downloadLink, $dest)

    # Create directory for gcloud installation
    New-Item -Path "$HOME" -Name "gcloud" -ItemType "directory" -Force

    Write-Output "Extracting gcloud archive on machine...."

    Expand-Archive -Path $dest -DestinationPath "$baseDir\gcloud-extracted"

    Write-Output "Finished extracting gcloud archive on machine...."
    Write-Output "Install gcloud sdk on machine in quiet mode...."
    .$baseDir\gcloud-extracted\google-cloud-sdk\install.bat -q
}

Write-Output "gcloud bin dir: $gcloudDir"

$path = $env:path
$pathEscaped = [regex]::Escape($path)
$gcloudDirEscaped = [regex]::Escape($gcloudDir)

Write-Output "Current path env: $path"

if (!("$pathEscaped" -Match "$gcloudDirEscaped")) {
    Write-Output "gcloud not found on path"
    Write-Output "Setting gcloud to path"
    $env:path += ";$gcloudDir"
    Write-Output "Path variable updated to: $env:path"
}

# Installation is complete, now let's setup gcloud
gcloud auth login
gcloud init
gcloud version
gcloud config set project $gcloud_project_id
gcloud firebase test android models list