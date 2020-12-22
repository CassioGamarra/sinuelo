const mysql = require('mysql');
require('dotenv/config');

//Cria uma conexão com o MySQL
/*Obs.: modificado o USER por USERNAME pois o parametro USER no enviroment busca
o usuário do sistema com base no parâmetro (root, user)*/
const connection = mysql.createConnection({
    host: process.env.HOST,
    user: process.env.ADMIN,
    password: process.env.PASSWORD,
    database: process.env.DATABASE
});
//Verifica se é possível conectar com o MySQL
connection.connect((err)=> {
    if(err){
        console.log("Erro ao conectar no banco de dados!" + err)
    }else{  
        console.log(`MySQL conectado [${process.env.PORT}]`);
    }
});

module.exports = connection;