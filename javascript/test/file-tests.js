// Maybe use .strict
const assert = require("assert");
const fs = require("fs");
const path = require("path");

// Find your module using the --pah_home variable and the default name 'preys-and-hunters'

const pahHome = process.env.npm_config_pah_home.endsWith("/")
  ? process.env.npm_config_pah_home
  : process.env.npm_config_pah_home + "/";
const pah = require(pahHome + "preys-and-hunters");

describe("File Tests", function () {
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

    it("should produce the expected output (" + test + ")", () => {
      pah.main(testInput);

      assert.equal(
        expectedOutput.trimRight(),
        stdOutHook.captured().trimRight(),
        "The GUI output for test " + test + " is not as expected."
      );
    });
  }
});

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
