export async function fetchCarsByUserId(userId) {
    const baseUrl = process.env.REACT_APP_API_BASE_URL;
    const res = await fetch(`${baseUrl}/api/cars/user/${userId}`);
    if (!res.ok) throw new Error("Erro ao buscar carros");
    return await res.json();
  }
  

  export async function addCar(carData) {
    const baseUrl = process.env.REACT_APP_API_BASE_URL;
    const res = await fetch(`${baseUrl}/api/cars`, {
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
    const baseUrl = process.env.REACT_APP_API_BASE_URL;
    const res = await fetch(`${baseUrl}/api/cars/${id}`, {
      method: 'DELETE'
    });
    if (!res.ok) throw new Error("Erro ao remover carro");
  }

  export async function updateCar(carId, updatedData) {
    const baseUrl = process.env.REACT_APP_API_BASE_URL;
    const res = await fetch(`${baseUrl}/api/cars/${carId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedData),
    });
  
    if (!res.ok) throw new Error("Erro ao atualizar carro");
    return await res.json();
  }

  
  
  