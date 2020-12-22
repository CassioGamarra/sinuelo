const conn = require('../../database/connection'); //Import connection
const bcrypt = require('bcryptjs'); //Import bcrypt
const jwt = require('jsonwebtoken');
require('dotenv/config');

//Gera um token que expira em 12 horas -> 43200
function generateToken(params = {}) {
  return jwt.sign(params, process.env.AUTHKEY, {
    expiresIn: 43200,
  });
}

module.exports = {
  async create(req, res) {
    const { login, password } = req.body;
    var token; 
    if (login && password && login.length < 100 && password.length < 200) { 
      //Cria o SQL para consulta
      const sql = `SELECT ID_USUARIO, SENHA FROM USUARIO WHERE LOGIN = '${login}'`;
      await conn.query(sql, (err, rows) => {
        if (err) throw err
        if (!rows[0]) {
          res.json({
            statusCode: 404,
            title: "Erro",
            message: "Seu usuário não foi encontrado ou não está ativo!"
          })
        } else {
          const result = bcrypt.compareSync(password, rows[0].SENHA);
          if (result) {
            token = generateToken({ id: rows[0].ID_USUARIO, adm: rows[0].ADM });
            res.json({
              statusCode: 200,
              token: token
            })

          } else {
            res.json({
              statusCode: 403,
              title: "Erro",
              message: "Senha incorreta!"
            })
          }

        }
      })
    } else {
      res.status(404).json({
        statusCode: 400,
        message: "Preencha os campos obrigatórios."
      });
    }
  }
}