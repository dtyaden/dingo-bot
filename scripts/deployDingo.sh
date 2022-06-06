echo $dingo
export target="dtyaden@45.56.67.151"
export key="~/.ssh/linode"
echo $target
echo $key
$dingo/gradlew distZip
cd ~/
if [ "$1" != "skip-copy" ]; then
    scp -i "$key" $dingo/Dingo-Bot-9000.zip $target:
fi 
ssh -i "$key" "$target" 'bash installDingo-Bot.sh'
