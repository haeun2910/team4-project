
async function getDrivingRoute(startLat, startLng, endLat, endLng) {
    const clientId = 'g24wojyn1l';
    const clientSecret = 'roKieXIZhqb8BA4sIasGgBscLRC4L9Q7h09q3wQQ';

    const url = `https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=${startLng},${startLat}&goal=${endLng},${endLat}&option=trafast`;

    try {
        let response = await fetch(url, {
            headers: {
                'X-NCP-APIGW-API-KEY-ID': clientId,
                'X-NCP-APIGW-API-KEY': clientSecret
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch driving route from NCloud Directions API');
        }

        let data = await response.json();
        console.log("Route information:", data);
        return data;
    } catch (error) {
        console.error('Error fetching driving route:', error);
    }
}
