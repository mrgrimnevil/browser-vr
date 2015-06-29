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
    if ( NAVI.util.isGradientString(bg) )
      return NAVI.setBackgroundGradient(bg);
    else if ( NAVI.util.isColorString(bg) )
      return NAVI.setBackgroundColor(bg);
    else if ( NAVI.util.isUrl(bg) )
      return NAVI.setBackgroundImage(bg);
    else
      return NAVI.setBackground(bg);
  },

  setBackgroundColor: function(color) {
    return _NAVI.setBackground(color);
  },

  setBackgroundGradient: function(bg) {
    return _NAVI.setBackgroundGradient(bg);
  },

  setBackgroundImage: function(imageUrl) {
    return _NAVI.setBackground(imageUrl);
  }

};

/* Scene */
NAVI.Scene = function(name) {
  this.name = name || '';
  this.id = NAVI.Scene._id++;

  this.children = [];
};

NAVI.Scene._id = 0;

NAVI.Scene.prototype.add = function(object) {
  _NAVI.addObjectToScene(this.name, object.name);
};

NAVI.Scene.prototype.remove = function(object) {
  _NAVI.removeObjectFromScene(this.name, object.name);
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


/* Object3D types */
NAVI.Plane = function(name) {
  NAVI.Object3D.call(this, name, 'plane');
};
NAVI.Plane.prototype = Object.create( NAVI.Object3D.prototype );

NAVI.Box = function(name) {
  NAVI.Object3D.call(this, name, 'cube');
};
NAVI.Box.prototype = Object.create( NAVI.Object3D.prototype );

NAVI.Sphere = function(name) {
  NAVI.Object3D.call(this, name, 'sphere');
};
NAVI.Sphere.prototype = Object.create( NAVI.Object3D.prototype );

NAVI.Cylinder = function(name) {
  NAVI.Object3D.call(this, name, 'cylinder');
};
NAVI.Cylinder.prototype = Object.create( NAVI.Object3D.prototype );


/* util */
NAVI.util = {
  isColorString: function(string) {
    return (string[0] == '#');
  },

  isGradientString: function(string) {
    return (string.indexOf(',') !== -1);
  },

  isUrl: function(string) {
    return true;
  }
};

/* Page context processing */
NAVI.process = function() {
  NAVI.processMeta();
};

NAVI.processMeta = function() {
  var metas = document.getElementsByTagName("meta");

  for (i = 0; i < metas.length; i++) {
    var meta = metas[i];
    var name = meta.getAttribute("name");

    if (name === "vr-background") {
      var content = meta.getAttribute("content");
      NAVI.setBackground( content );
    }
  }
};

// TODO: invoke on page load event
setTimeout(function(){
  NAVI.process();
}, 3000);

