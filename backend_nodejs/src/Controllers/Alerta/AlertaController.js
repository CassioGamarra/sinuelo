const conn = require('../../database/connection');
require('dotenv/config');
module.exports = { 
  //Cadastrar Alerta
  async verificaRegistroAlerta(req, res) {
    const idUsuario = req.id;
    const { id_animal, } = req.body;
    const sqlSelect = `SELECT ID_ANIMAL FROM ALERTA WHERE ID_ANIMAL = ${id_animal}`;

    await conn.query(sqlSelect, (err, rows) => {
      if (err) throw err;
      if (!rows[0]) {
        register();
      } else {
        res.json({
          statusCode: 200,
          message: "Animal já cadastrado!",
          error: true
        });
      }
    });

    async function register() {
      const data = {
        COD_BRINCO: cod_brinco,
        NOME: nome,
        DATA_NASCIMENTO: data_nasc,
        PESO_ORIGINAL: peso_original,
        OBSERVACOES: observacoes
      };

      const sqlInsert = `INSERT INTO PRESTADOR SET DATA_CADASTRO = CURDATE(), ?`;

      await conn.query(sqlInsert, data, (err, result) => {
        if (err) throw err;
        else {
          res.json({
            statusCode: 200,
            cadastrado: true,
            message: "Cadastro realizado com sucesso!",
          });
        }
      });
    }
  },

  async buscarAnimal(req, res) {
    const id = req.idPrestador;
    const sqlSelect = `
    SELECT NOME, TELEFONE, CEP, NUMERO, LOGRADOURO, COMPLEMENTO, BAIRRO, CIDADE, UF
    FROM PRESTADOR U  
    WHERE ID_PRESTADOR = ${id}
  `;

    await conn.query(sqlSelect, (err, rows) => {
      if (err) throw err;
      else {
        res.json(rows);
      }
    });
  },

  async atualizarRegistroAnimal(req, res) {
    const idUsuario = req.id;
    const idPrestador = req.idPrestador;
    const { nome, telefone, cep, numero, logradouro, complemento, bairro, cidade, uf } = req.body;
    const arrayData = [nome, telefone, cep, numero, logradouro, complemento, bairro, cidade, uf, idPrestador];

    const sqlUpdate = `
    UPDATE PRESTADOR SET NOME = ?, TELEFONE = ?, CEP = ?, NUMERO = ?, LOGRADOURO = ?, COMPLEMENTO = ?, BAIRRO = ?, CIDADE = ?, UF = ? 
    WHERE ID_PRESTADOR = ?
  `;

    await conn.query(sqlUpdate, arrayData, (err, results) => {
      if (err) throw err;
      else {
        verificaCadastro();
      }
    });

    async function verificaCadastro() {
      const sqlSelect = `SELECT CADASTRO_COMPLETO FROM USUARIO WHERE ID_USUARIO = ${idUsuario}`;
      await conn.query(sqlSelect, (err, rows) => {
        if (err) throw err;
        if (rows[0].CADASTRO_COMPLETO === 0) {
          updateUsuario();
        } else {
          res.json({
            statusCode: 200,
            title: "Atualizando Perfil",
            atualizado: true,
            message: "Cadastro atualizado com sucesso!",
          });
        }
      });
    }

    async function updateUsuario() {
      const sqlUpdate = `UPDATE USUARIO SET CADASTRO_COMPLETO = 1 WHERE ID_USUARIO = ${idUsuario}`;
      await conn.query(sqlUpdate, (err, results) => {
        if (err) throw err;
        else {
          res.json({
            statusCode: 200,
            title: "Atualizando Perfil",
            atualizado: true,
            message: "Cadastro atualizado com sucesso!",
          });
        }
      });
    }
  },

  async listarAnimais(req, res) {

  }
};