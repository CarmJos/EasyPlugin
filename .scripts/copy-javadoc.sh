rm -rf docs
mkdir -vp docs

DOC_URL="target/site/apidocs/"

for FILE in easyplugin-*; do

  if test -e "$FILE/$DOC_URL"; then
    ls -l "$FILE/*"

    MODULE_FILE="docs/${FILE:11}/"

    mkdir -vp "$MODULE_FILE"

    cp -vrf "$FILE/$DOC_URL*" "$MODULE_FILE"

  fi

  echo "$FILE"

done

cp -vrf .documentation/javadoc/JAVADOC-README.md docs/README.md
cp -vrf .documentation/javadoc/index.html docs/index.html
