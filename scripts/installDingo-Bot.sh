killall dingo
dingoZip="Dingo-Bot-9000.zip"
echo $dingoZip
cp ~/$dingoZip $dingoInstallationDir
unzip -o $dingoInstallationDir/$dingoZip -d $dingoInstallationDir
$dingoInstallationDir/Dingo-Bot-9000/bin/dingo-bot
