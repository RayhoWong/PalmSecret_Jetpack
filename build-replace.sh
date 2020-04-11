#!/bin/bash
# create by zhangliang 2019
######################################################################
#                           配置正式环境，对工程配置进行替换
#                           需要替换项：
#                           1、baselib values    ->commerce_cfg.xml
#                           2、baselib raw       ->micro_services_config.json
#                           3、app               ->google-services.json
#                           4、baselib package 	 ->AppConstants.kt
#                           5、build.gradle      ->applicationId
######################################################################

# replace commerce_cfg.xml
FILE_PATH_commerce_cfg_debug='./baselib/src/main/res/values/commerce_cfg.xml'
FILE_PATH_commerce_cfg_release='./release_config/commerce_cfg.xml'

# replace micro_services_config.json
FILE_PATH_micro_services_config_debug='./baselib/src/main/res/raw/micro_services_config.json'
FILE_PATH_micro_services_config_release='./release_config/micro_services_config.json'

# replace google-services.json
FILE_PATH_google_services_debug='./app/google-services.json'
FILE_PATH_google_services_release='./release_config/google-services.json'

#replace Product.kt
FILE_PATH_Product_debug='./baselib/src/main/java/com/palmapp/master/baselib/Product.kt'
FILE_PATH_Product_release='./release_config/Product.kt'

# replace applicationId
APP_gradle_path='./app/build.gradle'
APP_applicationId_debug='com.palm.test'
APP_applicationId_release='com.palmsecret.horoscope'



# commerce_cfg.xml 替换
echo "commerce_cfg.xml 开始替换"
cp -r ${FILE_PATH_commerce_cfg_release} ${FILE_PATH_commerce_cfg_debug}
echo "commerce_cfg.xml ok!"

# micro_services_config.json 替换
echo "micro_services_config.json"
cp -r ${FILE_PATH_micro_services_config_release} ${FILE_PATH_micro_services_config_debug}
echo "micro_services_config.json ok!"

# google-services.json 替换
echo "google-services.json 开始替换"
cp -r ${FILE_PATH_google_services_release} ${FILE_PATH_google_services_debug}
echo "google-services.json ok!"


# AppConstants.kt 替换
echo "AppConstants.kt 开始替换"
cp -r ${FILE_PATH_Product_release} ${FILE_PATH_Product_debug}
echo "Product.kt ok!"

# applicationId 替换
echo "applicationId 开始替换"
pwd
echo $APP_gradle_path

sed -i "s/$APP_applicationId_debug/$APP_applicationId_release/g" $APP_gradle_path
echo "applicationId ok!"

echo -e "\033[32m 替换完成 ！\033[0m"
















