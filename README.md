# rhbq-supported-extension-generator Project

## Build status

[![CI Testing](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/test.yaml/badge.svg)](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/test.yaml)
[![Linux x86_64 release](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/linux-x86_64-release.yaml/badge.svg)](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/linux-x86_64-release.yaml)
[![MacOS x86_64 release](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/macos-x86_64-release.yaml/badge.svg)](https://github.com/tqvarnst/rhbq-supported-extension-generator/actions/workflows/macos-x86_64-release.yaml)


This application can be used to generate Markdown on which extensions are supported, tech-preivew, or developer support. 

The binary applications are available for Linux and Mac and can be downloaded from the [release page](https://github.com/tqvarnst/rhbq-supported-extension-generator/releases).

# Installation
The binary can be installed anywhere and executed from the command line. However, the recommended process is this.

1. Download the latest release from the [release page](https://github.com/tqvarnst/rhbq-supported-extension-generator/releases)
2. Copy the binary to a directy on your file system

        cp ~/Downloads/rhbq-gen-[linux|macos]-x86_64 ~/bin/rhbq-gen

3. Make the file executable

        chmod +x ~/bin/rhbq-gen

4. Use the help command to learn how to use the app

        rhbq-gen --help

