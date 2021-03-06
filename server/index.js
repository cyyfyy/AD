var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

function player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
}

server.listen(8080, function(){
	console.log("Server is up...");
});

io.on('connection', function(socket){
	console.log("Player connected!");
	socket.emit('socketID', {id: socket.id}); //sends to one socket
	socket.emit('getPlayers', players);
	socket.broadcast.emit('newPlayer', {id: socket.id}); //sends to all _other_ connected clients
	players.push(new player(socket.id, 0, 0));
	socket.on('playerMoved', function(data){
	    data.id = socket.id;
	    socket.broadcast.emit('playerMoved', data);

        console.log("playerMoved: " +
         "ID: " + data.id +
         "X: " + data.x +
         "Y: " + data.y);

	    for(var i = 0; i < players.length; i++){
	        if(players[i].id == data.id){
	            players[i].x = data.x;
	            players[i].y = data.y;
	        }
	    }
	});
	socket.on('disconnect', function(){
		console.log("Player disconnected!");
		socket.broadcast.emit('playerDisconnected', {id: socket.id});
		//find the player that disconnected and remove them from the world list
		for(var i = 0; i < players.length; i++){
		    if(players[i].id = socket.id){
                players.splice(i, 1);
		    }
		}
	});
});