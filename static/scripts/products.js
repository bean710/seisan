var response = 0;
var xmlHttp = new XMLHttpRequest();

function submitData() {
  response = 0;

  sku = -1;
  name = "";
  name = $("input[name='name']").val();
  sku = $("input[name='sku']").val();
  console.log(name + "  " + sku);

  if (sku != "") {
    theUrl = "/productAPI?sku=" + sku;
  } else if (name != "") {
    theUrl = "/productAPI?name=" + name;
  } else {
    theUrl = "/productAPI";
  }

  xmlHttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      var res = this.response;
      console.log(typeof res + "    " + res);
      var ret = JSON.parse(res);
      console.log(ret);
      display(ret);
    }
  }

  xmlHttp.open( "GET", theUrl, true);
  xmlHttp.send();
}

function display(data) {

  var tableString = "";
  $("#results").text("");

  tableString += "<table id=\"products\"> <tr><th>sku</th> <th>name</th> <th>price</th> <th>quantity</th> <th>description</th></tr>";
  data.forEach(function(prod){
    console.log(JSON.stringify(prod));
    tableString += "<tr><td>" + prod.sku + "</td><td>" + prod.name + "</td><td>" + prod.price + "</td><td>" + prod.qty + "</td><td>" + prod.description + "</td></tr>";
  });
  tableString += "</table>";

  $("#results").append(tableString);
}
