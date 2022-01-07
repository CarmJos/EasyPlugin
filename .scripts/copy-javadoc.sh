rm -rf docs
mkdir -vp docs

for i in easyplugin*; do
  if test -e "$i/target/apidocs/"; then
    cp -vrf "$i/target/apidocs/*" "docs/$1/"
  fi
done

cp -vrf .documentation/javadoc/JAVADOC-README.md docs/README.md
cp -vrf .documentation/javadoc/index.html docs/index.html
