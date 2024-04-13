# Run PostgreSQL in Docker for local development

## Docker Compose

### Start

```shell
docker-compose --project-name="pgih-db" up -d
```

### Stop

```shell
docker-compose --project-name="pgih-db" down
```

## Explore volumes

### List all volumes

```shell
docker volume ls
```

### Delete specified volume

```shell
docker volume rm pgih-db_pgih-db-data
```
