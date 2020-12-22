const conn = require('../../database/connection');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
require('dotenv/config');

var nodemailer = require('nodemailer');
var transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.GMAIL_USER,
    pass: process.env.GMAIL_PASS
  }
});

//Gera um token que expira em 1 hora
function generateToken(params = {}) {
  return jwt.sign(params, process.env.AUTHKEY, {
    expiresIn: 3600,
  });
}

module.exports = {
  //Registrar a senha do prestador
  async registerPassword(req, res) {
    const id = req.id;
    const { password, passwordConfirm } = req.body;
    const sqlSelect = `SELECT ID_USUARIO FROM USUARIO WHERE ID_USUARIO = ${id} AND ATIVO = 1`;

    await conn.query(sqlSelect, (err, rows) => {
      if (err) throw err
      if (rows[0]) {
        res.json({
          statusCode: 400,
          title: "Ativação de Cadastro",
          message: "Seu cadastro já foi ativado!"
        });
      } else { 
        if (password.length > 200 || passwordConfirm.length > 200) {
          res.status(404).json({
            statusCode: "-1",
            message: "Verifique os dados e tente novamente mais tarde!"
          });
        }
        else {
          if (password !== passwordConfirm) {
            res.json({
              statusCode: 404,
              title: "Erro",
              message: "Senhas não são iguais!"
            });
          }
          else {
            criarSenha();
          }
        }
      }
    }); 
    async function criarSenha() {
      if (password != "") {
        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync(password, salt);
        const sqlUpdate = `UPDATE USUARIO SET SENHA = '${hash}', ATIVO = 1 WHERE ID_USUARIO = ${id}`;
        await conn.query(sqlUpdate, (err, rows) => {
          if (err) throw err
          else {
            res.json({
              statusCode: 200,
              title: "Ativação de Cadastro",
              message: "Cadastro ativado com sucesso!"
            });
          }
        });
      }
    }
  },

  //Para solicitar um reset de senha
  async forgetPassword(req, res) {
    const { email } = req.body;
    var token;

    if (email && email.length < 200) {
      const sql = `SELECT ID_USUARIO, EMAIL FROM USUARIO WHERE EMAIL ='${email}'`;
      await conn.query(sql, (err, rows) => {
        if (err) throw err
        if (!rows[0]) {
          res.json({
            statusCode: 404,
            title: "Erro",
            message: "Usuário não encontrado"
          });
        } else {
          enviarEmail(rows);
        }
      });
    } else {
      res.status(404).json({
        statusCode: "-1",
        message: "Preencha todos os campos obrigatórios!"
      });
    }

    async function enviarEmail(data) {
      const sql = `UPDATE USUARIO SET RESET_SENHA = 1 WHERE ID_USUARIO = ${data[0].ID_USUARIO}`
      await conn.query(sql, (err, rows) => {
        if (err) throw err
        else {
          token = generateToken({ id: data[0].ID_USUARIO, tipo: 'resetPassStore' });

          var mailOptions = {
            from: process.env.GMAIL_USER,
            to: data[0].EMAIL,
            subject: 'Redefinição de senha',
            html: `<p>Olá,<br/>${data[0].EMAIL}, enviamos o link para o resetar sua senha, ele expira em 1 hora.
                          <br/>Link: <a href="http://sulservices.com.br/conta/recuperar/${token}">Resetar minha senha</a> </p>
                          <br>
                          <p> Caso não tenha solicitado, desconsidere este contato</p>`
                          //<p><img src="cid:avmb@avmb.com.br"/></p>`,
            // An array of attachments
            /*attachments: [
              // File Stream attachment
              {
                filename: 'avmb.png',
                path: './src/img/avmb.png',
                cid: 'avmb@avmb.com.br' // should be as unique as possible
              }
            ]*/
          };

          sendEmail = transporter.sendMail(mailOptions, function (error, info) {
            if (error) {
              console.log(error);
            } else {
              console.log('Email sent: ' + info.response);
            }
          });
          res.json({
            statusCode: 200,
            title: "Redefinição de senha",
            message: "Solicitação efetuada com sucesso, confira no e-mail cadastrado o link para resetar sua senha. Lembre-se de conferir a caixa de spam.",
          });
        }
      });
    }
  },
  //Para criar a nova senha
  async createNewPassword(req, res) {
    const { password, passwordConfirm } = req.body;
    const id = req.id;
    if (password.length > 200 || passwordConfirm.length > 200) {
      res.status(404).json({
        statusCode: "-1",
        message: "Verifique os dados e tente novamente mais tarde!"
      });
    }
    else {
      if (password !== passwordConfirm) {
        res.json({
          statusCode: 404,
          title: "Erro",
          message: "Senhas não são iguais!"
        });
      }
      else {
        const sql = `SELECT RESET_SENHA FROM USUARIO WHERE ID_USUARIO = ${id}`
        await conn.query(sql, (err, rows) => {
          if (err) throw err
          if (rows[0].RESET_SENHA === 0) {
            res.json({
              statusCode: 404,
              title: "Erro",
              message: "Seu token expirou!"
            });
          }
          else {
            criarSenha();
          }
        });
      }
    }
    async function criarSenha() {
      if (password != "") {
        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync(password, salt);
        const sql = `UPDATE USUARIO SET SENHA = '${hash}', RESET_SENHA = 0 WHERE ID_USUARIO = ${id}`;
        await conn.query(sql, (err, rows) => {
          if (err) throw err
          else {
            res.json({
              statusCode: 200,
              title: "Alteração de senha",
              message: "Nova senha cadastrada com sucesso!"
            });
          }
        });
      }
    }
  },
  async getDadosUsuario(req, res) {
    const id = req.id; 

    const sqlSelect = `
      SELECT EMAIL
      FROM USUARIO   
      WHERE ID_USUARIO = ${id}
    `;

    await conn.query(sqlSelect, (err, rows) => {
      if (err) throw err; 
      else {
        res.json(rows);
      }
    });
  },
  //Atualiza os dados de usuário
  async atualizarUsuario(req, res) {
    const { email, password } = req.body;
    const id = req.id;
    const sql = `SELECT EMAIL FROM USUARIO WHERE ID_USUARIO <> ${id} AND EMAIL = '${email}'`;
    await conn.query(sql, (err, rows) => {
      if (err) throw err;
      if (rows[0]) {
        res.json({
          statusCode: 400,
          title: "Erro",
          message: "Email já cadastrado",
          error: true
        });
      } else {
        update();
      }
    });

    async function update() {
      if (password) {
        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync(password, salt);
        const arrayData = [email, hash, id];

        const sql = `UPDATE USUARIO SET EMAIL = ?, SENHA = ? WHERE ID_USUARIO = ?`;
        await conn.query(sql, arrayData, (err, results, fields) => {
          if (err) throw err
          res.json({
            title: "Atualizar Usuário",
            message: "Usuário atualizado com sucesso",
            atualizado: true,
            updatePass: true
          })
        });
      } else {
        const arrayData = [email, id];
        const sql = `UPDATE USUARIO SET EMAIL = ? WHERE ID_USUARIO = ?`;
        await conn.query(sql, arrayData, (err, results, fields) => {
          if (err) throw err
          res.json({
            title: "Atualizar Usuário",
            message: "Usuário atualizado com sucesso",
            atualizado: true
          })
        });
      }
    }
  }
};