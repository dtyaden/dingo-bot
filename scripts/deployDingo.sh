cd $dingo
export target="david@104.154.183.123"
export key=".ssh/google-dingo"
echo $target
echo $key
gradle distZip
cd ~/
if [ "$1" != "skip-copy" ]; then
    scp -i "$key" $dingo/Dingo-Bot-9000.1.1.zip $target:
fi 
ssh -i "$key" "$target" 'bash install-Dingo.sh'
ssh -i "$key" "$target" 'bash dingo-start-stop.sh'
