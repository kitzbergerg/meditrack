# Build state
FROM node:16-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Run stage
FROM nginx:1.25.3-alpine
COPY --from=build /app/dist/angular-frontend /usr/share/nginx/html
