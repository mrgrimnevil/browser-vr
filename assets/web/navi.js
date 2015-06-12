/* NAVI JS client library */
'use strict';

var NAVI = {

	version: '0.1',

	getValue: function(key) {
		return _NAVI.getValue(key);
	},

	setValue: function(key, value) {
		return _NAVI.setValue(key, value);
	},

	/* background */
	setBackground: function(bg) {
		if ( NAVI.util.isColorString(bg) )
			return NAVI.setBackgroundColor(bg);
		else //
			return NAVI.setBackgroundImage(bg);
	},

	setBackgroundColor: function(color) {
		return _NAVI.setBackgroundColor(color);
	},

	setBackgroundImage: function(imageUrl) {
		return _NAVI.setBackgroundImage(imageUrl);
	},


	/* objects */
	createObject: function(name, type) {
		return _NAVI.createObject(name, type);
	},

	moveObject: function(name, x,y,z) {
		return _NAVI.moveObject(name, type);
	},

	translateObject: function(name, x,y,z) {
		return _NAVI.translateObject(name, x,y,z);
	},

	scaleObject: function(name, x,y,z) {
		return _NAVI.scaleObject(name, x,y,z);
	},

	rotateObject: function(name, x,y,z) {
		return _NAVI.rotateObject(name, x,y,z);
	},

	rotationObject: function(name, w,x,y,z) {
		return _NAVI.rotationObject(name, w,x,y,z);
	}


	/* util */
	util: {
		isColorString: function(string) {
			return (string[0] == '#');
		}

		isUrl: function(string) {
			return true;
		}
	}

};
