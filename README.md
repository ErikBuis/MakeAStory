# MakeAStory
MakeAStory is a Java-based program to run text-based interactive stories. Stories consist of chapters, which in turn consist of sections. At the end of each section, the reader can choose out of a list of choices the author made up. Each choice leads to another section.

## Compilation
Compile the program by using:
```bash
javac /Path/To/MakeAStory/*/*.java
```
Compilation is necessary only once, because new stories will be automatically detected and compiled at runtime.

## Usage
You can use this program as a reader or an author. To use this program as a reader, run:
```bash
java /Path/To/MakeAStory/RunAStoryTerminal
```
To use this program as an author, refer to [Write a story](#write-a-story).

## Write a story
A story is made up of one or more chapters, each of which contain sections which can be separated into paragraphs. Each time the reader presses the enter/return key, they are shown a new paragraph. At the end of each section, the author must specify at least one option for the reader to choose and jump to. This jump can point to another section in the same chapter, the first (default) or a specific section in another chapter or the end of the story. For each section, the author is also able to specify a certain paragraph within it.

### Setup
Write your own story by inserting your own directory into the SavedStories directory. Your directory must contain a `main.properties` file and a text file for each chapter in the story. The `main.properties` file must contain the following data:
```properties
title=<Title of your story here>
author=<Your name>
chapters=<Comma-separated list of chapters to include>
```
Only the text files mentioned in the `chapters` argument are compiled and included into the story. If you didn't finish a chapter (yet) or want to include other files in the directory, just don't include them in this argument.

### Commands
`!section; INTEGER sectionIndex` Start a section. Sections must begin at index 0, incrementally counting upwards for each new section in a chapter.

`!next` to start a new paragraph. This can be used to decrease the amount of text offered at once, encouraging a more natural way to read.

`!opt; STRING optionName [; STRING chapterName] [; INTEGER sectionIndex [; INTEGER paragraphIndex]]` One of the options for the reader to choose at the end of a section. The optional arguments provide a location to jump to if the reader chooses this option. A section within a specific chapter or a paragraph within a certain section can also be specified.

`!optifread/!optifnotread [; STRING chapterName]; INTEGER sectionIndex [; INTEGER paragraphIndex]; <same options as opt>` Only show this option to the reader if they have (for `!optifread`) or have not (for `!optifnotread`) read the specified section. Just like with `!opt`, a section within another chapter as well as a paragraph within a section could also be specified.

`!author; STRING authorComment` A comment that will be completely ignored by the compiler.

`!logbook; STRING logbookParagraph` Used primarily in long stories, this command can be used to remind a reader of the story so far if the they decided to stop reading temporarily.
