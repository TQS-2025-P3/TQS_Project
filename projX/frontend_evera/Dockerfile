FROm node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install
 

COPY . .
RUN npm run build

EXPOSE 3000

# Serve the build folder
CMD ["npx", "serve", "-s", "build", "-l", "3000"]