const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:3000',
    
    viewportWidth: 1280,
    viewportHeight: 720,
    
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    
    retries: {
      runMode: 2,    
      openMode: 0    
    },
    
    screenshotOnRunFailure: false,
    video: false,
    videoCompression: 32,
    
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/e2e.js',
    fixturesFolder: 'cypress/fixtures',
    screenshotsFolder: 'cypress/screenshots',
    videosFolder: 'cypress/videos',
    
    setupNodeEvents(on, config) {
      
      on('task', {
        log(message) {
          console.log(message)
          return null
        }
      })
    },
    
    experimentalSessionAndOrigin: true,
    
    chromeWebSecurity: false
  }
})