
import './commands'

beforeEach(() => {
  cy.intercept('GET', '**/api/cars/**', { fixture: 'cars.json' }).as('getCars')
  cy.intercept('POST', '**/api/cars', { statusCode: 201 }).as('addCar')
  cy.intercept('PUT', '**/api/cars/**', { statusCode: 200 }).as('updateCar')
  cy.intercept('DELETE', '**/api/cars/**', { statusCode: 204 }).as('deleteCar')
  
  cy.task('log', ` Iniciando teste: ${Cypress.currentTest.title}`)
})

afterEach(() => {
  if (Cypress.currentTest.state === 'failed') {
    cy.screenshot(`failed-${Cypress.currentTest.title}`)
  }
  
  const status = Cypress.currentTest.state === 'passed' ? '✅' : '❌'
  cy.task('log', `${status} Teste finalizado: ${Cypress.currentTest.title}`)
})

Cypress.on('uncaught:exception', (err, runnable) => {
  if (err.message.includes('ResizeObserver loop limit exceeded')) {
    return false
  }
  
  if (err.message.includes('Non-Error promise rejection captured')) {
    return false
  }
  
  return true
})

Cypress.config('defaultCommandTimeout', 8000)
Cypress.config('requestTimeout', 10000)

if (Cypress.env('DEBUG')) {
  Cypress.on('command:start', (c) => {
    console.log(`🔍 Executando: ${c.attributes.name}`, c.attributes)
  })
}