FROM node:16 AS build-stage
WORKDIR /app
COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build --prod

FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build-stage /app/dist/ui-book-network /usr/share/nginx/html

EXPOSE 80

