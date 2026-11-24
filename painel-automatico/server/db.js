const { Pool } = require('pg');
const fs = require('fs');
const path = require('path');

const certificado = fs.readFileSync(
    path.join(__dirname, '..', 'certs', 'supabase-ca.crt'),
    'utf8'
);

const pool = new Pool({
    host: process.env.DB_HOST,
    port: Number(process.env.DB_PORT || 6543),
    database: process.env.DB_NAME,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,

    ssl: {
        rejectUnauthorized: true,
        ca: certificado
    },

    max: 5,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 10000
});

pool.on('error', (error) => {
    console.error('Erro inesperado na conexão com o banco:', error.message);
});

module.exports = pool;