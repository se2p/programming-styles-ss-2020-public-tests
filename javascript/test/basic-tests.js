// Maybe use .strict
const assert = require('assert');

// Find your module using the --pah_home variable and the default name 'preys-and-hunters'

const fs = require("fs");
const path = require("path");

// Find your module using the --pah_home variable and the default name 'preys-and-hunters'
const pahHome = process.env.npm_config_pah_home.endsWith("/")
  ? process.env.npm_config_pah_home
  : process.env.npm_config_pah_home + "/";
const pah = require(pahHome + "preys-and-hunters");

const configFile = path.join(pahHome, "config.ini")

describe('Basic Tests', function() {
    
    describe('runs correct version of node', function(){
        it('Version of node must be v10.20.1', function() {
            assert.equal(process.version, 'v10.20.1');
        });
    });

    // https://stackoverflow.com/questions/21587122/mocha-chai-expect-to-throw-not-catching-thrown-errors
    // assert.throws( FunctionThatShouldThrow_AssertionError, assert.AssertionError )
    describe('runs with exceptions', function() {

        beforeEach("Ensure no config.init before execution", function(){
            // Make sure the config.ini file is not there
            if( fs.existsSync(configFile)){
                fs.unlinkSync(configFile);
            }
        });

        afterEach("Ensure no config.init after execution", function(){
            // Make sure the config.ini file is not there
            if( fs.existsSync(configFile)){
                fs.unlinkSync(configFile);
            }
        });

        it('Preys and Hunters should raise an exception if config.ini is missing', function() {
            var pah_inputs = [1, 2, 3]
            try {
                pah.main(pah_inputs)
                assert.fail("No error was raised")
            } catch (err) {
                if( err instanceof assert.AssertionError ){
                    throw err
                }
            }
        });
        
        it('Preys and Hunters should raise an exception if config.ini is empty', function() {
            // Make sure there's an empty config.ini file (see https://flaviocopes.com/how-to-create-empty-file-node/)
            fs.closeSync(fs.openSync(configFile, 'w'))

            var pah_inputs = [1, 2, 3]
            try {
                pah.main(pah_inputs)
                assert.fail("No error was raised")
            } catch (err) {
                if( err instanceof assert.AssertionError ){
                    throw err
                }
            }
        });

        it('Preys and Hunters should raise an exception if config.ini does not declare the required "pluginName" option', function() {
            // Make sure that config file exists but does not contain the required option
            fs.writeFileSync(configFile, `;pluginName=default\n\npluginId=fixed`)

            var pah_inputs = [1, 2, 3]
            try {
                pah.main(pah_inputs)
                assert.fail("No error was raised")
            } catch (err) {
                if( err instanceof assert.AssertionError ){
                    throw err
                }
            }
        });

        it('Preys and Hunters should raise an exception if config.ini points to non existing plugins', function() {
            // Make sure there's the required config in the file but the wrong value
            fs.writeFileSync(configFile, 'pluginName=foobar')

            var pah_inputs = [1, 2, 3]
            try {
                pah.main(pah_inputs)
                assert.fail("No error was raised")
            } catch (err) {
                if( err instanceof assert.AssertionError ){
                    throw err
                }
            }
        });
    });

    describe('runs without exceptions()', function() {
        beforeEach("Ensure no config.init before execution", function(){
            // Make sure the config.ini file is not there
            if( fs.existsSync(configFile)){
                fs.unlinkSync(configFile);
            }
        });

        afterEach("Ensure no config.init after execution", function(){
            // Make sure the config.ini file is not there
            if( fs.existsSync(configFile)){
                fs.unlinkSync(configFile);
            }
        });

        it('Preys and Hunters should raise no exception', function() {
            // Make sure that a valid config file exists
            fs.writeFileSync(configFile, `pluginName=default`)

            var pah_inputs = [1, 2, 3]
            pah.main(pah_inputs)
        });
    });
});