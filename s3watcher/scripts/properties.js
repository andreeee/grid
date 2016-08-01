const fs = require('fs');
const ini = require('ini');

function load(app) {
    const propertiesFile = "/configs/etc/gu/${app}.properties";
    const properties = ini.parse(fs.readFileSync(propertiesFile).toString());
    return properties;
}

module.exports = {
    load: load
};
