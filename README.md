# WizardStepView

## Example

![exanoke](https://s9.postimg.org/4sxwygl5r/wizard_Step_View.gif)

## Getting Started

[![jitpack](https://jitpack.io/v/DEADMC/WizardStepView.svg)](https://jitpack.io/#DEADMC/WizardStepView)

Add it in your root build.gradle at the end of repositories:

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
  
```groovy
	dependencies {
	        compile 'com.github.DEADMC:WizardStepView:1.0'
	}
```

## Usage

### Add Layout

```xml
    <com.deadmc.wizardstepview.WizardStepView
        android:id="@+id/wizardStepView"
	android:layout_height="wrap_content"
        android:layout_width="match_parent"
        app:activeColor="@color/colorPrimary"
        app:inactiveColor="#8c8c8c"
        app:cirleRadius="16dp"
        app:textInactiveColor="#ffffff"
        app:textActiveColor="#ffffff"
        />
```

### Set click listener

```java
        wizardStepView.setClickListener(new WizardClickListener() {
            @Override
            public void click(int position) {

            }
        });
```

### Additional features:
You can bind it to ViewPager
```java
wizardStepView.setViewPager(viewPager);
```

Set current position
```java
wizardStepView.setViewPagerPosition(3);
```

Set steps count
```java
wizardStepView.setStepsCount(5);
```
