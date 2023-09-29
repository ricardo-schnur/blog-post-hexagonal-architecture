# MongoDB setup

## Installation

We use a Docker Compose file to easily start up a local MongoDB instance.
Just make sure you have Docker (with the Compose plugin) installed and run the command

```
docker-compose -f mongodb-compose.yaml up
```

in your terminal to set up an instance. Note
that we expose the default database `test` on its default port `27017` with credentials `root / example`. You can, of
course, adapt
this in the compose file.

## Shell access

You can use the [Mongo shell][Shell] to connect to it from your terminal with the command:

```
mongosh --authenticationDatabase admin --username root --password example
```

You can use

- `db.listCollections()` to see all your tables,
- `db.<TABLE_NAME>.find()` to show all entries of a table, and
- `db.<TABLE_NAME>.deleteMany({})` to delete all entries from a table.

After the initial setup, you will of course not have any collections, but the application will create the
collection `taskEntity` on-th-fly when needed for the first time.

[Shell]: https://www.mongodb.com/try/download/shell
