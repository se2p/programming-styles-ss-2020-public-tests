// Maybe use .strict
const assert = require('assert');

// Find your module using the --pah_home variable and the default name 'preys-and-hunters'
const pahHome = process.env.npm_config_pah_home + 'preys-and-hunters'
const pah = require(pahHome)

describe('Basic Test', function() {
    
    describe('runs correct version of node()', function(){
        it('Version of node must be v10.20.1', function() {
            assert.equal(process.version, 'v10.20.1');
        });
    });

    describe('runs without exceptions()', function() {
        it('Preys and Hunters should raise no exception', function() {
            var pah_inputs = [1, 2, 3]
            pah.main(pah_inputs)
        });
  });
});