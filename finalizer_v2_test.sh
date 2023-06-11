#!/bin/bash

# Run your first command here
./finalizer -i app-debug.aab -f guardit4j.fin -o finalizer-final --verbose ; \

# Run your second command here
/Users/mac/Library/Android/sdk/build-tools/30.0.3/zipalign -v -f -p 4 /Users/mac/Downloads/finalizer/finalizer-final/app-debug-unaligned-unsigned-protected.aab aligned.aab ; \

# Run your third command here
/Users/mac/Library/Android/sdk/build-tools/30.0.3/apksigner sign -v --ks vpbank_key.jks --min-sdk-version 21 --ks-pass pass:cYb@L9va --out signed.aab aligned.aab ; \

java -jar /Users/mac/Downloads/bundle/bundletool.jar build-apks --local-testing --bundle /Users/mac/Downloads/finalizer/signed.aab --output /Users/mac/Downloads/finalizer/my_app.apks ; \

java -jar /Users/mac/Downloads/bundle/bundletool.jar install-apks --apks /Users/mac/Downloads/finalizer/my_app.apks ; \

rm aligned.aab signed.aab.idsig my_app.apks app-debug.aab ; \
rm -r finalizer-final 

echo "APK generate successfully!"