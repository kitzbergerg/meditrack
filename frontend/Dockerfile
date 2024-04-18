# Build state
FROM node:21-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Run stage
FROM nginx:latest
COPY --from=build /app/dist/frontend /usr/share/nginx/html
