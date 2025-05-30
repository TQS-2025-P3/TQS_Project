export async function fetchCarsByUserId(userId) {
    const res = await fetch(`http://localhost:8080/api/cars/user/${userId}`);
    if (!res.ok) throw new Error("Erro ao buscar carros");
    return await res.json();
  }
  

  export async function addCar(carData) {
    const res = await fetch('http://localhost:8080/api/cars', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(carData)
    });
  
    if (!res.ok) throw new Error("Erro ao adicionar carro");
    return await res.json();
  }
  
  export async function deleteCar(id) {
    const res = await fetch(`http://localhost:8080/api/cars/${id}`, {
      method: 'DELETE'
    });
    if (!res.ok) throw new Error("Erro ao remover carro");
  }

  export async function updateCar(carId, updatedData) {
    const res = await fetch(`http://localhost:8080/api/cars/${carId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedData),
    });
  
    if (!res.ok) throw new Error("Erro ao atualizar carro");
    return await res.json();
  }

  
  
  