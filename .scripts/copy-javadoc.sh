rm -rf docs
mkdir -vp docs

DOC_URL="target/site/apidocs"

for FILE_NAME in easyplugin-*; do

  if test -e "$FILE_NAME/$DOC_URL"; then
    ls -l "$FILE_NAME"

    MODULE_FILE="docs/${FILE_NAME:11}/"

    mkdir -vp "$MODULE_FILE"

    cp -vrf "$FILE_NAME/$DOC_URL/*" "$MODULE_FILE"

  fi

  echo "$FILE_NAME"

done

cp -vrf .documentation/javadoc/JAVADOC-README.md docs/README.md
cp -vrf .documentation/javadoc/index.html docs/index.html
