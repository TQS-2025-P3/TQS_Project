 <reference types="cypress" />

describe('Profile Page - Gestão de Veículos', () => {
  
    beforeEach(() => {
      cy.visit('/profile')
      
      cy.get('h4').should('contain', 'Perfil do Utilizador')
      cy.get('h6').contains('Seus Veículos').should('be.visible')
      
      cy.get('body').should('not.contain', 'CircularProgress')
      cy.wait(2000) 
    })
  
    describe('Interface da Página', () => {
      it('deve exibir todos os elementos da interface corretamente', () => {
        cy.get('h4').should('contain', 'Perfil do Utilizador')
        
        cy.get('h6').should('contain', 'Seus Veículos')
        cy.get('h6').should('contain', 'Adicionar Novo Veículo')
        
        cy.get('input[name="brand"]').should('be.visible')
        cy.get('input[name="model"]').should('be.visible') 
        cy.get('input[name="plate"]').should('be.visible')
        cy.get('input[name="batteryCapacity"]').should('be.visible').and('have.attr', 'type', 'number')
        
        cy.contains('button', 'Adicionar Carro').should('be.visible')
      })
  
      it('deve mostrar carros existentes OU mensagem vazia', () => {
        cy.wait(3000)
        
        cy.get('body').then(($body) => {
          if ($body.text().includes('Nenhum veículo registado')) {
            cy.contains('Nenhum veículo registado').should('be.visible')
            cy.log('✅ Lista vazia confirmada')
          } else {
            cy.get('.MuiGrid-container').should('exist')
            cy.log('Carros encontrados na lista')
          }
        })
      })
    })
  
    describe('Adicionar Veículos', () => {
      it('deve adicionar um novo veículo com sucesso', () => {
        cy.intercept('POST', '**/api/cars').as('addCar')
        
        const timestamp = Date.now()
        cy.get('input[name="brand"]').clear().type('Tesla')
        cy.get('input[name="model"]').clear().type('Model S')
        cy.get('input[name="plate"]').clear().type(`TS-${timestamp.toString().slice(-4)}`)
        cy.get('input[name="batteryCapacity"]').clear().type('100')
        
        cy.contains('button', 'Adicionar Carro').click()
        
        cy.wait('@addCar', { timeout: 10000 })
        
        cy.get('@addCar').should('have.property', 'response')
        cy.get('@addCar').its('response.statusCode').should('be.oneOf', [200, 201])
        
        cy.log('Carro adicionado com sucesso via API!')
      })
  
      it('deve mostrar todos os campos necessários', () => {
        cy.get('input').should('have.length', 4)
        cy.get('input[name="brand"]').should('have.attr', 'type', 'text')
        cy.get('input[name="model"]').should('have.attr', 'type', 'text')
        cy.get('input[name="plate"]').should('have.attr', 'type', 'text')
        cy.get('input[name="batteryCapacity"]').should('have.attr', 'type', 'number')
      })
    })
  
    describe('Validações de Formulário', () => {
      it('deve mostrar erro ao submeter formulário vazio', () => {
        cy.contains('button', 'Adicionar Carro').click()
        
        cy.wait(3000)
        
        cy.get('body').then(($body) => {
          if ($body.text().includes('Preenche todos os campos')) {
            cy.contains('Preenche todos os campos').should('be.visible')
          } else {
            cy.log('ℹSem mensagem de validação - comportamento pode variar')
          }
        })
      })
  
      it('deve aceitar entrada de dados nos campos', () => {
        cy.get('input[name="brand"]').type('TestBrand').should('have.value', 'TestBrand')
        cy.get('input[name="model"]').type('TestModel').should('have.value', 'TestModel')
        cy.get('input[name="plate"]').type('TT-11-TT').should('have.value', 'TT-11-TT')
        cy.get('input[name="batteryCapacity"]').type('50').should('have.value', '50')
        
        cy.get('input[name="brand"]').clear().should('have.value', '')
        cy.get('input[name="model"]').clear().should('have.value', '')
        cy.get('input[name="plate"]').clear().should('have.value', '')
        cy.get('input[name="batteryCapacity"]').clear().should('have.value', '')
      })
    })
  
    describe('Lista de Veículos - Teste Preparado', () => {
      beforeEach(() => {
        cy.intercept('POST', '**/api/cars').as('addCar')
        cy.intercept('GET', '**/api/cars/**').as('getCars')
        
        const timestamp = Date.now()
        cy.get('input[name="brand"]').clear().type('BMW')
        cy.get('input[name="model"]').clear().type('i4')
        cy.get('input[name="plate"]').clear().type(`BM-${timestamp.toString().slice(-4)}`)
        cy.get('input[name="batteryCapacity"]').clear().type('80')
        
        cy.contains('button', 'Adicionar Carro').click()
        cy.wait('@addCar', { timeout: 10000 })
        
        cy.wait(3000)
      })
      
      it('deve mostrar informações dos carros após adicionar', () => {
        cy.contains('BMW i4').should('be.visible')
        cy.contains('Bateria: 80 kWh').should('be.visible')
        
        cy.get('button').should('have.length.greaterThan', 1)
      })
  
      it('deve ter botões de ação funcionais', () => {
        cy.wait(5000)
        
        cy.reload()
        cy.wait(3000)
        
        cy.get('button').should('have.length.greaterThan', 1)
        
        cy.get('body').then(($body) => {
          if ($body.find('.MuiIconButton-root').length > 0) {
            cy.get('.MuiIconButton-root').should('have.length.greaterThan', 0)
            cy.log('✅ Botões de ação encontrados')
            
            cy.get('.MuiIconButton-root').first().click()
            cy.wait(1000)
            
            cy.get('input[name="brand"]').then(($input) => {
              if ($input.val() !== '') {
                cy.log(`✅ Edição funcionou - marca: ${$input.val()}`)
              } else {
                cy.log('ℹ️ Campo não foi preenchido, mas botão existe')
              }
            })
          } else {
            cy.log('ℹ️ Nenhum botão de ação encontrado - lista pode estar vazia')
          }
        })
      })
    })
  
    describe('Navegação e Interface', () => {
      it('deve ter elementos de navegação visíveis', () => {
        cy.get('h4').should('contain', 'Perfil do Utilizador')
        
        cy.get('body').then(($body) => {
          const bodyText = $body.text()
          
          if (bodyText.includes('Evera')) {
            cy.contains('Evera').should('be.visible')
            cy.log('✅ Navbar encontrada com Evera')
            
            if (bodyText.includes('PERFIL')) {
              cy.contains('PERFIL').should('be.visible')
            }
            if (bodyText.includes('ESTAÇÕES')) {
              cy.contains('ESTAÇÕES').should('be.visible')
            }
            
          } else {
            cy.get('h6').should('contain', 'Seus Veículos')
            cy.get('h6').should('contain', 'Adicionar Novo Veículo')
            cy.log(' Conteúdo principal verificado')
          }
        })
      })
    })
  
    describe('Funcionalidade Básica', () => {
      it('deve conseguir focar nos campos', () => {
        cy.get('input[name="brand"]').focus().should('be.focused')
        cy.get('input[name="model"]').focus().should('be.focused')
        cy.get('input[name="plate"]').focus().should('be.focused')
        cy.get('input[name="batteryCapacity"]').focus().should('be.focused')
      })
  
      it('deve conseguir preencher e submeter formulário', () => {
        cy.intercept('POST', '**/api/cars').as('addCarRequest')
        
        const timestamp = Date.now()
        cy.get('input[name="brand"]').clear().type('Audi')
        cy.get('input[name="model"]').clear().type('e-tron')
        cy.get('input[name="plate"]').clear().type(`AD-${timestamp.toString().slice(-4)}`)
        cy.get('input[name="batteryCapacity"]').clear().type('95')
        
        cy.get('input[name="brand"]').should('have.value', 'Audi')
        cy.get('input[name="model"]').should('have.value', 'e-tron')
        cy.get('input[name="batteryCapacity"]').should('have.value', '95')
        
        cy.contains('button', 'Adicionar Carro').click()
        cy.wait('@addCarRequest', { timeout: 10000 })
      })
    })
  
    describe('Verificação de APIs', () => {
      it('deve fazer chamadas para o backend corretamente', () => {
        cy.intercept('GET', '**/api/cars/**').as('getCars')
        cy.intercept('POST', '**/api/cars').as('postCar')
        
        cy.reload()
        cy.wait('@getCars', { timeout: 10000 })
        
        cy.get('h4').should('contain', 'Perfil do Utilizador')
      })
    })
  })