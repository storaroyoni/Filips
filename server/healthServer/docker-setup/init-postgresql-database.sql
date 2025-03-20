SELECT 'CREATE DATABASE healthdb' WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'healthdb') \gexec
GRANT ALL PRIVILEGES ON DATABASE healthdb TO "user";