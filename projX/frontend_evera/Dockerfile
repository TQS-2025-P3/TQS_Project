FROM node:20-alpine

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Accept the API URL as a build-time argument
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL

# Copy project files
COPY . .

# Write the API URL to a .env file so React can use it during build
RUN echo "REACT_APP_API_URL=$REACT_APP_API_URL" > .env

# Build the React app
RUN npm run build

EXPOSE 3000

# Serve the build folder
CMD ["npx", "serve", "-s", "build", "-l", "3000"]
