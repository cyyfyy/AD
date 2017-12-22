var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

function player(id, x, y){
    this.id = id;
}

server.listen(8080, function(){
	console.log("Server is up...");
});

io.on('connection', function(socket){
    if(players.length >= 5){
        socket.emit('reject');
        socket.disconnect();
        return;
    }
	console.log("Player connected! " + socket.id);
	socket.emit('socketID', {id: socket.id}); //sends to one socket
	socket.emit('getPlayers', players);
	socket.broadcast.emit('newPlayer', {id: socket.id}); //sends to all _other_ connected clients
	players.push(new player(socket.id));
	console.log("New player total: " + players.length);
	socket.on('submit', function(data){
	    data.id = socket.id;
	    socket.broadcast.emit('submit', data);

        console.log("playerSubmit: " +
         "ID: " + data.id +
         "Vote: " + data.x);

	    for(var i = 0; i < players.length; i++){
	        if(players[i].id == data.id){
	            players[i].x = data.x;
	            players[i].y = data.y;
	        }
	    }
	});
	socket.on('disconnect', function(){
		console.log("Player disconnected! " + socket.id);
		socket.broadcast.emit('playerDisconnected', {id: socket.id});
		//find the player that disconnected and remove them from the world list
		for(var i = 0; i < players.length; i++){
		    if(players[i].id = socket.id){
                players.splice(i, 1);
                break;
		    }
		}
		console.log("New player total: " + players.length);
	});
});