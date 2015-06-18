/* NAVI JS client library */
'use strict';

var NAVI = {

	version: '0.1',

	showToast: function(text) {
		return _NAVI.showToast(text);
	},

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
	}

};

/* Object3D */
NAVI.Object3D = function(name, type) {
	this.name = name;
	this.type = type;

	this.id = NAVI.Object3D._id++;
	//this.uuid = _NAVI.getUUID();

	this.parent = false;
	this.children = [];

	this.position = [0,0,0];
	this.rotation = [0,0,0,0];
	this.scale = [1,1,1];

	this._native_id = null;

	this._init();
};

NAVI.Object3D._id = 0;

NAVI.Object3D.prototype._init = function() {
	_NAVI.createObject(this.name, this.type);
};

NAVI.Object3D.prototype.setPosition = function(x,y,z) {
	this.position = [x,y,z];
	_NAVI.moveObject(this.name, x,y,z);
};

NAVI.Object3D.prototype.translate = function(x,y,z) {
	_NAVI.translateObject(this.name, x,y,z);
};

NAVI.Object3D.prototype.setRotation = function(w,x,y,z) {
	this.rotation = [w,x,y,z];
	_NAVI.rotationObject(this.name, w,x,y,z);
};

// rotate angle around axis:[x,y,z]
NAVI.Object3D.prototype.rotate = function(angle, x,y,z) {
	_NAVI.rotateObject(this.name, angle, x,y,z);
};

NAVI.Object3D.prototype.setScale = function(x,y,z) {
	this.scale = [x,y,z];
	_NAVI.scaleObject(this.name, x,y,z);
};

NAVI.Object3D.prototype.destroy = function() {
	//_NAVI.destroyObject(this.name);
};

/* util */
NAVI.util = {
	isColorString: function(string) {
		return (string[0] == '#');
	},

	isUrl: function(string) {
		return true;
	}
};
