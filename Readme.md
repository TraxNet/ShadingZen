# ShadingZen - A 2D/3D Engine for Android 

ShadingZen is a complete game engine targeting android devices supporting OpenGL 2.0 and licensed under the permissive MIT License. It was designed to be easy to use and extend, trying to offer the same good ideas that made Cocos2D so popular. Behind the scenes the concept of parallel tasks is exploited to take full advantage of modern  multicore mobile CPUs. 

It has been used commercially in [Kids 3D Cube](https://play.google.com/store/apps/details?id=org.traxnet.kidscube) a logic game for kids available at Google Play.

## Easy to use

The setup required to start working with the engine has been reduced to the creation of an Android Activity and a few more classes that hold the game logic. ShadingZen provides a GLSurfaceView that has been fine tuned to support different display properties and hardware. 

## Future

The are some features like shadow casting and post processing effects that require a few underlying changes to the library. It also lacks documentation and is probably the area that requires more effort to make ShadingZen shine.

Something is planned to offer a tool-chain to work with 3DS Studio MAX and offer some basic artist workflow. The library currently can use static .obj meshes.

## License

Copyright (c) 2012 Oscar Blasco Maestro and Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.