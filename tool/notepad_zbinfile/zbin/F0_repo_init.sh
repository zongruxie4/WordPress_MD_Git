#!/bin/bash
CurPath_Begin=$(pwd)
echo "PATH_Begin="$CurPath_Begin
##################### delete replace operation Begin #####################
cd ..
Code_Dir=$(pwd)
echo "Code_Dir="$Code_Dir
#=========== Code Dir Operation Begin ===========
#TODO-LIST-Begin  
# cp -fr  ./xxxx.java    $CurPath_Begin/external/wpa_supplicant_8/xxx.java   



# TODO-LIST-END
cd $CurPath_Begin
#=========== Code Dir Operation END ===========
##################### /packages/apps/Settings/  Begin #####################
cd $CurPath_Begin/packages/apps/Settings/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /frameworks/opt/net/wifi/  Begin #####################
cd $CurPath_Begin/frameworks/opt/net/wifi/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /frameworks/base Begin #####################
cd $CurPath_Begin/frameworks/base/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /external/wpa_supplicant_8  Begin #####################
cd $CurPath_Begin/external/wpa_supplicant_8 
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /hardware/qcom/gps Begin #####################
cd $CurPath_Begin/hardware/qcom/gps/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /vendor/qcom/proprietary/gps  Begin #####################
cd $CurPath_Begin/vendor/qcom/proprietary/gps/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /vendor/qcom/proprietary/gps-release  Begin #####################
cd $CurPath_Begin/vendor/qcom/proprietary/gps-release/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /vendor/qcom/proprietary/location  Begin #####################
cd $CurPath_Begin/vendor/qcom/proprietary/location/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /device/qcom/common  Begin #####################
cd $CurPath_Begin/device/qcom/common
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /motorola/security/sepolicy  Begin #####################
cd $CurPath_Begin/motorola/security/sepolicy/
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin
##################### /vendor/qcom/opensource/wlan/qcacld-3.0  Begin #####################
cd $CurPath_Begin/vendor/qcom/opensource/wlan/qcacld-3.0
#TODO-LIST-Begin  
# git cherry-pick 
# git reset --hard 



# TODO-LIST-END
CUR_GIT_PATH=$(pwd)
echo "CUR_GIT_PATH="$CUR_GIT_PATH
cd $CurPath_Begin

#################################################### Custom Operation Begin ##########################################################################

############################ MSI Delete Operation 
cd $CurPath_Begin
## date_time=`date +%Y%m%d_%H%M%S`
## mv ./out  ./out_"$date_time"
## mv ./kernel_platform/out ./kernel_platform/out_"$date_time"
## mv ./release  ./release_"$date_time"



############################ MSI Init Operation
cd $CurPath_Begin
## repo init custom &&  repo --trace sync -cdf -j2     ## repo sync failed check 



############################ MSI  Build Operation 
cd $CurPath_Begin
## source /opt/conf/moto.conf  

#===========================================================================


#=============================  Vendor Delete Operation 
cd $CurPath_Begin
## date_time=`date +%Y%m%d_%H%M%S`
## mv ./out  ./out_"$date_time"
## mv ./kernel_platform/out ./kernel_platform/out_"$date_time"
## mv ./release  ./release_"$date_time"


#============================= Vendor Init Operation
cd $CurPath_Begin
##  repo init custom  &&  repo --trace sync -cdf -j2     ## repo sync failed check 

#=============================  Vendor  Build Operation 
cd $CurPath_Begin
## source /opt/conf/moto.conf


#################################################### Custom Operation End   #########################################################################


cd $CurPath_Begin
CurPath_End=$(pwd)
echo "PATH_End="$CurPath_End
