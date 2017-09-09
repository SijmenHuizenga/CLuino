# CLion + Arduino = CLuino

This plugin adds run configurations to CLion that enable you to build and upload arduino sketches. CLuino uses the [arduino-builder](https://github.com/arduino/arduino-builder) project that is also used by the arduino ide. This way you can write your code in CLion, and compile and upload it using the official compiler-configuration provided by the Arduino Project.

The vision of this project is to make Arduino development in CLion just as easy as in the Arduino IDE.

## Getting started
First install the [offical arduino ide](https://www.arduino.cc/en/Main/Software) and CLion. Than download the latest release CLuino.jar from this project's [releases](https://github.com/SijmenHuizenga/CLuino/releases) page. Install the downloaded jar as plugin in CLion. See [this tutorial](https://www.jetbrains.com/help/clion/installing-a-plugin-from-the-disk.html) for instructions.

Next, create a new project in CLion. Select language level c++11 and replace the content of `main.cpp` with some arduino-compatible code. For example:
```arduino
void setup() {
    Serial.begin(9600);
}

void loop(){
    Serial.println(millis());
    delay(1000);
}
```

**WARNING: ALL DEFAULT VALUES ARE SETUP FOR THE ARDUINO UNO. TO USE OTHER BOARDS PLEASE READ THE ADVANCED CONFIGURATION SECTION**

Now go into [run configurations](https://www.jetbrains.com/help/clion/creating-and-editing-run-debug-configurations.html) and add a arduino->compile configuration. Select the main file of your program. In this example this is `main.cpp`. 
All other configurations should work out of the box. The configurations `hardwares` or `tools` are often automaticly found on your filesystem. If these fields are not automaticly filled in than you must mannually select the hardwares and tools location. See the chapter Advanced Configuration for more information on how to do this. When everything is configured you can run the run-configuration!

If you see no errors in the console than the configuration is correct. To see the build results, look in the `out/arduino-build` directory. There are two `.hex` files. One with and one without the bootloader. These hex files can be uploaded to an arduino.

To upload the hex file, create a new arduino->upload run configuration. Select which hex file you want to upload and the serial device that is connected to the arduino. The avrdude config path shoud be automaticly detected on your filesystem. But if this is not the case you have to select this file mannualy. See the chapter Advanced Configuration for more information on how to do this. 

Now you are setup to compile and upload your code in a professional ide!

If you do not want to press two buttons (compile and upload) every time you change your code, you can add a 'before-launch' action on the upload run configuraiton. Select 'run another configuration' and choose the corresponding compile run configuration. This way running the upload configration will first compile the code.

## Code Support

CLion will not understand things like `Serial.println()` or `millis()`. As you might have noticed, CLuino only adds run configurations. These run configurations are fully seperate from the code intelligence that CLion performs. If you want code intelligence like auto-completion and code checking you have to add the arduino header files to your project. There are two ways to do this. The easy way and the hard way. Your choise.

### The easy way
1. Download [Arduino.h](https://github.com/SijmenHuizenga/CLuino/blob/master/Arduino.h) and add this file to your project in a directory called `library`.
2. In your `CMakeList.txt` after `project()` add the following line: `include_directories("library")`
3. In every file you want to use arduino-code include the arduino header file using the line `#include <Arduino.h>`

### The hard way
Do some dificult stuff in your `CMakeList.txt` to include all header files in the project. If you have figured out a way to make this work, please contribute and add some tutorial here!

## Advanced Configration

### Compile
//todo

### Upload
//todo

## Different boards
//todo

## FAQ

### compile error: `arduino: Unknown package`
The compiler can not find the package named `arduino` that contains the board specific build configurations. The first part of the board code (arduino:avr:uno) references a hardware package. The hardware packages are specified in the `Hardware` section. So this error means that the `arduino` package cannot be found in the specified `Hardware` folders.

To fix this you can ether specify a different board package code (for example `archlinux-arduino:avr:uno` on Arch Linux) that actually exists or install the package that is needed. 

Installing a new package can easely be done through the Arduino IDE. Go into Tools -> Boards -> Board Manager and install the board package that you want to use. The package is installed in the following directory: `~/.arduino15/packages/`. Make sure you add this directory in the `Hardwares` section so it is picked up by the compiler.
