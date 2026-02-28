BASE=/Users/deepakpanwar/Documents/004_PersonalDev/EnterpriseApp

# Root
mkdir -p $BASE/{gradle,.github/workflows}

# Build logic
mkdir -p $BASE/build-logic/{convention/src/main/kotlin/convention,src/main/kotlin}

# Core modules
for mod in common data domain navigation ui network testing; do
  mkdir -p $BASE/core/$mod/src/{main/kotlin/com/enterprise/core/$mod,test/kotlin/com/enterprise/core/$mod}
done

# Feature modules
for feat in home detail profile search settings; do
  mkdir -p $BASE/feature/$feat/src/{main/kotlin/com/enterprise/feature/$feat/{mvi,ui,di},test/kotlin/com/enterprise/feature/$feat,androidTest/kotlin/com/enterprise/feature/$feat}
done

# App
mkdir -p $BASE/app/src/{main/{kotlin/com/enterprise/app/{di,navigation,ui},res/{xml,values,drawable,mipmap-hdpi}},androidTest/kotlin/com/enterprise/app,test/kotlin/com/enterprise/app}

# Baseline profile
mkdir -p $BASE/baseline-profiles/src/main/kotlin/com/enterprise/baseline

echo "All directories created"
find $BASE -type d | wc -l
