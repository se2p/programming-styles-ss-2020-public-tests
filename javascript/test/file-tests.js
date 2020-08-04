// Maybe use .strict
const assert = require("assert");
const fs = require("fs");
const path = require("path");

// Find your module using the --pah_home variable and the default name 'preys-and-hunters'
const pahHome = process.env.npm_config_pah_home.endsWith("/")
  ? process.env.npm_config_pah_home
  : process.env.npm_config_pah_home + "/";
const pah = require(pahHome + "preys-and-hunters");

// This points to the confiig file in the correct location
const configFile = path.join(pahHome, "config.ini")

// Constants for board matching
const linesPerBoard = 6

describe("Common File Tests. Tag: Assignment1, Assignment2, Assignment3, Assignment4", function () {
  // Capturing stdout (for later)
  let stdOutHook;

  beforeEach(() => {
    stdOutHook = captureStream(process.stdout);
  });

  afterEach(() => {
    stdOutHook.unhook();
  });

  // Setup and read tests
  const testDir = process.env.npm_config_test_data;
  if (!testDir || testDir === "") {
    throw new Error("Runtime variable test_data is not test");
  }

  const fullPath = path.join(process.cwd(), testDir);
  const testDataFolder = fs.lstatSync(fullPath);
  if (!testDataFolder.isDirectory()) {
    throw new Error(
      "Test data folder (" + __dirname + testDataFolder + ") is not a folder."
    );
  }

  const inputs = {};
  const outputs = {};
  const filesInDir = fs.readdirSync(fullPath);
  for (let fileName of filesInDir) {

    if( fileName.includes("_fixed_") ){
      // Here we care only about the files that correspond to the common output
      continue;
    }

    const testName = fileName
      .replace("_input.txt", "")
      .replace("_output.txt", "");
    const fileContent = fs
      .readFileSync(path.join(fullPath, fileName))
      .toString();

    if (fileName.endsWith("_input.txt")) {
      inputs[testName] = fileContent.split(" ");
    } else if (fileName.endsWith("_output.txt")) {
      outputs[testName] = fileContent;
    }
  }

  for (let test of Object.keys(inputs)) {
    if (!outputs.hasOwnProperty(test)) {
      throw new Error("No output file for input test: " + test);
    }

    const testInput = inputs[test];
    const expectedOutput = outputs[test];

    // set configuration:
    it("should produce the expected output (" + test + ")", () => {
      copyConfiguration(test)
      pah.main(testInput);

      expectedLines = expectedOutput.trimRight().split(/\r?\n/)
      actualLines = stdOutHook.captured().trimRight().split(/\r?\n/)

      assert.equal(expectedLines.length, actualLines.length, "Expected " + expectedLines.length +  " lines but got " + actualLines.length + " in the GUI output for test " + test + ".")
      
      numberOfBoards = expectedLines.length / linesPerBoard

      for (var i = 0; i < numberOfBoards; i++) {
        for (var j = 0; j < linesPerBoard; j++) {
          var currentExpectedLine = expectedLines[i * linesPerBoard + j]
          var currentActualLine = actualLines[i * linesPerBoard + j]
          // Check for equality, catch the assertion error and re-throw a pretty printed error.
          try {
            assert.equal(currentExpectedLine, currentActualLine)
          } catch(error) {
            prettyPrintBoardError(i, j, expectedLines, actualLines, error)
          }
        }
      }
    });
  }
});

describe("File Tests With Plugins. Tag: Assignment3", function () {
  // Capturing stdout (for later)
  let stdOutHook;

  beforeEach(() => {
    stdOutHook = captureStream(process.stdout);
  });

  afterEach(() => {
    stdOutHook.unhook();
  });

  // Setup and read tests
  const testDir = process.env.npm_config_test_data;
  if (!testDir || testDir === "") {
    throw new Error("Runtime variable test_data is not test");
  }

  const fullPath = path.join(process.cwd(), testDir);
  const testDataFolder = fs.lstatSync(fullPath);
  if (!testDataFolder.isDirectory()) {
    throw new Error(
      "Test data folder (" + __dirname + testDataFolder + ") is not a folder."
    );
  }

  const inputs = {};
  const outputs = {};
  const filesInDir = fs.readdirSync(fullPath);
  for (let fileName of filesInDir) {

    if( ! fileName.includes("_fixed_") ){
      // Here we care only about the files for the fixed plugin.
      continue;
    }

    const testName = fileName
      .replace("_input.txt", "")
      .replace("_output.txt", "");
    const fileContent = fs
      .readFileSync(path.join(fullPath, fileName))
      .toString();

    if (fileName.endsWith("_input.txt")) {
      inputs[testName] = fileContent.split(" ");
    } else if (fileName.endsWith("_output.txt")) {
      outputs[testName] = fileContent;
    }
  }

  for (let test of Object.keys(inputs)) {
    if (!outputs.hasOwnProperty(test)) {
      throw new Error("No output file for input test: " + test);
    }

    const testInput = inputs[test];
    const expectedOutput = outputs[test];

    // set configuration:
    it("should produce the expected output (" + test + ")", () => {
      copyConfiguration(test)
      pah.main(testInput);

      expectedLines = expectedOutput.trimRight().split(/\r?\n/)
      actualLines = stdOutHook.captured().trimRight().split(/\r?\n/)

      assert.equal(expectedLines.length, actualLines.length, "Expected " + expectedLines.length +  " lines but got " + actualLines.length + " in the GUI output for test " + test + ".")
      
      numberOfBoards = expectedLines.length / linesPerBoard

      for (var i = 0; i < numberOfBoards; i++) {
        for (var j = 0; j < linesPerBoard; j++) {
          var currentExpectedLine = expectedLines[i * linesPerBoard + j]
          var currentActualLine = actualLines[i * linesPerBoard + j]
          // Check for equality, catch the assertion error and re-throw a pretty printed error.
          try {
            assert.equal(currentExpectedLine, currentActualLine)
          } catch(error) {
            prettyPrintBoardError(i, j, expectedLines, actualLines, error)
          }
        }
      }
    });
  }
});

function copyConfiguration(testName) {
  // Replace the config file in the project folder to ensure the file is in the correct location
  if (testName.includes('fixed')) {
    fs.copyFileSync('./config_fixed.ini', configFile)
  } else {
    fs.copyFileSync('./config_default.ini', configFile)
  }

}

function prettyPrintBoardError(i, j, expectedLines, actualLines, oldError) {
  var firstDiffPos = 0;
  var currentExpectedLine = expectedLines[i * linesPerBoard + j]
          var currentActualLine = actualLines[i * linesPerBoard + j]
  while (currentExpectedLine[firstDiffPos] === currentActualLine[firstDiffPos]) firstDiffPos++;

  var expectedChar, actualChar
  expectedChar = (firstDiffPos < currentExpectedLine.length) ? currentExpectedLine[firstDiffPos] : "<end of line>" 
  actualChar = (firstDiffPos < currentActualLine.length) ? currentActualLine[firstDiffPos] : "<end of line>" 
  
  expectedLines[i * linesPerBoard + j] += "<"
  expectedLines.splice(i * linesPerBoard + linesPerBoard, 0, "^".padStart(firstDiffPos + 1,"-"))
  actualLines[i * linesPerBoard + j] += "<"
  actualLines.splice(i * linesPerBoard + linesPerBoard, 0, "^".padStart(firstDiffPos + 1,"-"))
  var contextArray = ["The expected and actual board differ for board number " + i, "Expected board:", "...", "Actual board: ", "...", "expected versus actual"]
  contextArray.splice(2, 1, expectedLines.slice(i * linesPerBoard, (i + 1) * linesPerBoard + 1).join("\n"))
  contextArray.splice(4, 1, actualLines.slice(i * linesPerBoard, (i + 1) * linesPerBoard + 1).join("\n"))
  contextArray.splice(5, 1, "expected char: '" + expectedChar + "' but actual char was: '" + actualChar + "'")

  var reformattedError = new assert.AssertionError({
    message: contextArray.join("\n"),
    actual: oldError.actual,
    expected: oldError.expected,
    operator: oldError.operator,
    stackStartFn: it
  })
  throw reformattedError
}

// Taken (and modified) from: https://stackoverflow.com/questions/18543047/mocha-monitor-application-output
function captureStream(stream) {
  var oldWrite = stream.write;
  var buf = "";
  stream.write = function (chunk, encoding, callback) {
    buf += chunk.toString(); // chunk is a String or Buffer
    // oldWrite.apply(stream, arguments);
  };

  return {
    unhook: function unhook() {
      stream.write = oldWrite;
    },
    captured: function () {
      return buf;
    },
  };
}
