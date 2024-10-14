
async function getPublicTransportInfo(startLat, startLng, endLat, endLng) {
    const apiKey = 'VyjPkBhOaGJPOxjlcqVe9jUmvlfR5zvMh3IwZBMoi8w';

    const url = `https://api.odsay.com/v1/api/searchPubTransPath?SX=${startLng}&SY=${startLat}&EX=${endLng}&EY=${endLat}&apiKey=${apiKey}&output=json`;

    try {
        let response = await fetch(url);
        if (!response.ok) {
            throw new Error('Failed to fetch data from ODsay API');
        }
        let data = await response.json();
        console.log("Public transport information", data);
        return data;
    } catch (error) {
        console.error("Error fetching public transport data:", error);
    }
}
