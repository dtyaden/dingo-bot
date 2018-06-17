cd $dingo
gradle distZip
cd ~/
scp -i .ssh/Dingo.pem $dingo/Dingo-Bot-9000.1.1.zip ubuntu@13.58.240.68:
ssh -i .ssh/Dingo.pem ubuntu@13.58.240.68 'bash installDingo.sh'
ssh -i .ssh/Dingo.pem ubuntu@13.58.240.68 'bash dingo-start-stop.sh'
