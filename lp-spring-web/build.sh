#!/bin/bash
#folder="./test"

exportDockerfile=()
exportName=()

typeset -l nameAtt

function contain()
{
    if echo "$1" | grep -w "$2" &>/dev/null; then
        echo true;
    else
        echo false;
    fi
}

function readfile ()
{
#这里`为esc下面的按键符号
 for file in `ls $1`
 do
    if [ $file == 'Dockerfile' ];then
        filePath=$1
        exportDockerfile+=($filePath);
        rootPath=${1##*/}
        exportName+=($rootPath);
        continue
    fi
    # 这里的-d表示是一个directory，即目录/子文件夹
    # 排除组级文件夹
    if [ $file == 'src' ] || [ "$file" == 'target' ]
    then
        continue
    fi
    if [ -d $1"/"$file ]
    then
        readfile $1"/"$file
    fi
 done
}

function dockerbuild() 
{
    echo 'build'
    index=0
    for file in ${exportDockerfile[@]} 
    do
        echo $file
        name=${exportName[$index]}
        echo ${name}
        mkdir $file/target/wqr-lib
        mkdir $file/target/lib
        nameAtt="${name}" 
        name=${nameAtt//-/.};
        name=${name//_/.};
        docker build -t registry.cn-hongkong.aliyuncs.com/julebuluo/$name:v1 $file
        if [ "$1" != "" ] && [ "$2" != "" ]; then 
            docker login --username=$1 --password=$2 registry.cn-hongkong.aliyuncs.com
            docker push registry.cn-hongkong.aliyuncs.com/julebuluo/$name:v1
        fi
    done
}


#函数定义结束，这里用来运行函数
readfile ../.
dockerbuild $1 $2 


#!/bin/bash
#folder="./test"
