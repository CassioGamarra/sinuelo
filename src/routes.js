//import das libs
const express = require('express')
const routes = express.Router();
const verifyJWT = require('./middlewares/auth');
const verifyCaptcha = require('./middlewares/recaptcha');

//import dos Controllers
//Login
const LoginController = require('./Controllers/Login/LoginController');
const UsuarioController = require('./Controllers/Usuario/UsuarioController');
//Animal
const AnimalController = require('./Controllers/Animal/AnimalController');
//Alerta
const AlertaController = require('./Controllers/Animal/AnimalController');
//Login
routes.post('/login', LoginController.create);
//Passwords
routes.post('/register/confirm', verifyCaptcha.verify, verifyJWT, UsuarioController.registerPassword);
routes.post('/recover', verifyCaptcha.verify, UsuarioController.forgetPassword);
routes.post('/renew', verifyCaptcha.verify, verifyJWT, UsuarioController.createNewPassword);
//Animal
routes.post('/animal/register', verifyJWT, AnimalController.verificarRegistroAnimal);
routes.post('/animal/buscar', verifyJWT, AnimalController.buscarAnimal);

module.exports = routes; 