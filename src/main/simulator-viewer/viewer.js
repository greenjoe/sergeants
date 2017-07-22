var app;

function handleFileSelect(evt) {
    var file = evt.target.files[0];
    var reader = new FileReader();
    reader.onload = function (event) {
        var uploadedString = event.target.result;
        var uploadedJson = JSON.parse(uploadedString);
        setup(uploadedJson)
    };
    reader.readAsText(file);
}


function setup(uploadedJson) {
    app.states = uploadedJson;
    app.index = 0;
    app.convert = mapStringToStateJson
}

function mapStringToStateJson(str) {
    var mapAttributes = str.split(";")
    var tick = parseInt(mapAttributes[0])
    var height = parseInt(mapAttributes[1])
    var width = parseInt(mapAttributes[2])
    var allFieldsString = mapAttributes[3];
    var fieldStrings = allFieldsString.split("|");
    var fields = [];
    for (var row = 0; row < height; ++row) {
        fields[row] = [];
        for (var col = 0; col < width; ++col) {
            fields[col] = [];
        }
    }
    for (var index = 0; index < fieldStrings.length; ++index) {
        var row = Math.floor(index / width);
        var col = index % width;
        fields[row][col] = fieldStringToJson(fieldStrings[index]);
    }

    return {
        height: height,
        width: width,
        tick: tick,
        fields: fields
    }
}
function fieldStringToJson(str) {
    var result = {};
    result.general = str.indexOf("g") >= 0;
    result.city = str.indexOf("c") >= 0;
    result.mountain = str.indexOf("m") >= 0;
    var armyPattern = /^\d+/;
    var armyMatch = armyPattern.exec(str) || ["0"];
    result.army = parseInt(armyMatch[0]);
    var ownerPattern = /o(\d+)/;
    var ownerMatch = ownerPattern.exec(str) || [null, null];
    result.owner = ownerMatch[1];
    return result;
}

document.body.onload = function () {
    app = new Vue({
        el: '#app',
        data: {
            states: null,
            index: 0,
            handleFileSelect: handleFileSelect,
            cellSize: 3
        }
    })
}