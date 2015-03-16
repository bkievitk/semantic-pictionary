# Tables #

There are four tables describing the complete raw Semantic Pictionary Data set.

  * Words: Contains the list of all words that are being used at the moment.

  * Player: Contains basic information on players.

  * Guess: Contains guesses made by participants about the correct labels for GeonObjects.

  * Models: Contains the information on each individual GeonObject.

# SQL Tables #

The SQL code for generating each of the tables can be found in the data folder with the file extensions ".sql". These files do not contain any of the actual data, but only the table formatting.

# CSV Tables #

The CSV files ".csv" contain the actual data from the project. These files can also be found in the data folder and their names correspond to a .sql file each. The CSV files do not contain headers but they should again match their corresponding .sql file except for the player.csv file which has had the password,email and name columns removed.

## Player Representations ##

NOTE: All ids are one greater than the correct value (they start indexing from 0) but the wiki editor can only start from 1.

  * Handedness
    1. Prefer not to say
    1. Right Handed
    1. Left Handed
    1. Ambidextrous.

  * Gender
    1. Prefer not to say
    1. Male
    1. Female.

  * Ethnicity
    1. Prefer not to say
    1. Hispanic or Latino
    1. Not Hispanic or Latino.

  * Race
    1. Prefer not to say
    1. American Indian/Alaska Native
    1. Asian
    1. Native Hawaiian or Other Pacific Islander
    1. Black or African American
    1. White
    1. More than one race.

## Guess Representations ##

  * guesserID: Refers to the playerID in the player table.
  * modelID: Refers to the modelID in the models table.
  * guessID: Refers to the wordID in the word table (the word guess by the guesser).
  * Direction
    * -1: left
    * 1: right
    * 0: unknown
    * 2: bad (see section on directionality)
  * gameType: What version was being played (see versions).
  * Time: Time that guess was submitted.

## Model Representations ##

NOTE: The wordID on games with the type "subjetPoolExperiment1" do not match the wordID on all other game types.

The model table contains the meat of the data. It contains some simple fields such as an id (modelID), the word that was requested to be represented (wordID), the player that made the model (playerID), the game type (see versions), and the time that the GeonObject was submitted. Most of the data are stored in the representation field which keeps a string representation of the GeonObject built.

As each GeonObject is a tree representation of the set of geons, the first thing to note is that the data set is divided into geons nested in geons. Thus the following structure: "[geon1[geon11[geon111](geon111.md)][geon12](geon12.md)]" represents a root geon "geon1" with two children, "geon11" and "geon12" with "geon11" having its own child, "geon111". The square brackets are always used to delimit different geons and are never used within the representation of a geon.

The data within each geon are separated into parts by commas. Thus a single geon representation will look something like this "[255,0,0,0,200,199,5]". Numbers are all integers but are spelled out in their ASCII form as characters. The number of parameters is slightly variable but fits the following pattern.

```
[red, green, blue, rotation, x scale, y scale, shape id, (childAttachmentPoint.x),  (childAttachmentPoint.y), (parentAttachmentPoint.x), (parentAttachmentPoint.y), (reserved)]
```

Red, green, and blue represent the color selected for the geon. These numbers vary from 0 to 255 for each color, defining a standard RGB color scheme. Rotation is specified in degrees clockwise from the geon's neutral position. The x and y scaling factors are stored as percentages. The shape id specifies which geon is being represented, such as a square, rectangle, or triangle. The specific definitions of these shapes are given in the appendix.

Attachment points are added if they are required. Thus they are given for all geons except the root one. The following directions and their corresponding values are given below. The child node refers to the geon in which the data block resides and the parent node represents the node that it is attached to (the one directly containing that node).

RIGHT: 1.
LEFT: -1.
CENTER: 0.
BOTTOM: -1.
TOP: 1.

Finally, more information can be stored after the last comma in the reserved section. This is reserved for future modifications of the models. In particular, it is used to define the motor attachments that are available in Semantic Charades (see relevant section).

# Handedness #

The handedness file contains measures of which direction the images were judged to be facing for a subset of [GeonObjects](GeonObjects.md). The handedness of the creator of that [GeonObject](GeonObject.md) as well as basic information about that user is also available on each line.

# Images #

The images.zip folder contains an image for each GeonObject created in the system. Images are 400 pixels by 400 pixels and are in the ".png" file format. They are generated using the GeonModel.thumbnail function using a background with a zero alpha channel and a scaling factor of 10. The images are sorted into folders that represent the names of the items the image is supposed to represent. The individual files are named in the following format {GeonObject type}/{GeonObject ID}.png. This should allow for easy referencing back into the other data tables if required.

# Serialized #

The data is also available in serialized form that is compatible with the Java code included. The newest data block is available as 8-21-2014.dat. This data block can be loaded through the ModelManager class as follows:
```
Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
```
Note: If the file is not found, the system will attempt to automatically generate a new data file by pinging the live database. As the ModelManager.adminPassword variable is set to blank in this code set, this will result in a failed call and the data set will not be generated.