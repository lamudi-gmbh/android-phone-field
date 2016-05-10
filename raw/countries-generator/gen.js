#!/usr/bin/env node

'use strict';

var _ = require("lodash"),
	_s = require("underscore.string"),
	fs = require("fs"),
	countries = require('./countries');

var output = "";



function generateCountriesClass() {
    var generatedClass = "";
    var a = function (l, indent) {
      if (_.isArray(l)) {
        _.each(l, function (eachL) {
          a(eachL, indent);
        });
      } else {
        generatedClass += (indent ? _s.repeat(" ", indent) : "") + (l||"") + "\n";
      }
    };

    a("package com.lamudi.phonefield;");
    a();
    a("import java.util.ArrayList;");
    a("import java.util.List;");
    a();
    a("public final class Countries {");
    a();
    a("public static final List<Country> COUNTRIES = new ArrayList<>();",4);
    a("static {",4);
    countries.forEach(function(country) {
    	a("COUNTRIES.add(new Country(\""+country.iso2+"\", \""+country.name+"\", "+country.dialCode+"));", 8);
	});
	a("}",4);
    a();
    a("}");

    return generatedClass;
  }



  var generated = generateCountriesClass();
  fs.writeFileSync("Countries.java", generated);