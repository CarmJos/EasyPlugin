rm -rf docs
mkdir -vp docs

DOC_URL="target/site/apidocs"

for FILE_NAME in easyplugin-*; do

  if test -e "$FILE_NAME/$DOC_URL"; then

    MODULE_FILE="docs/${FILE_NAME:11}/"

    mkdir -vp "$MODULE_FILE"

    cp -vrf "$FILE_NAME"/"$DOC_URL"/* "$MODULE_FILE"

  fi

done

cp -vrf .documentation/javadoc/JAVADOC-README.md docs/README.md
cp -vrf .documentation/javadoc/index.html docs/index.html
