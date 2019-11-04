function calcThisAmount(play, perf) {
    let thisAmount = 0;
    switch (play.type) {
        case "tragedy":
            thisAmount = 40000;
            if (perf.audience > 30) {
                thisAmount += 1000 * (perf.audience - 30);
            }
            break;
        case "comedy":
            thisAmount = 30000;
            if (perf.audience > 20) {
                thisAmount += 10000 + 500 * (perf.audience - 20);
            }
            thisAmount += 300 * perf.audience;
            break;
    }
    return thisAmount;
}

function calcThisCredits(perf, play) {
    let thisCredits = Math.max(perf.audience - 30, 0);
    if ("comedy" === play.type) thisCredits += Math.floor(perf.audience / 5);
    return thisCredits;
}

function calculateVolumeCredits(invoice, plays) {
    let volumeCredits = 0;
    for (let perf of invoice.performances) {
        let thisCredits = calcThisCredits(perf, plays[perf.playID]);
        volumeCredits += thisCredits;
    }
    return volumeCredits;
}

function calculateTotalAmount(invoice, plays) {
    let totalAmount = 0;
    for (let perf of invoice.performances) {
        totalAmount += calcThisAmount(plays[perf.playID], perf);
    }
    return totalAmount;
}

function calculateLines(invoice, plays) {
    let lines = [];
    for (let perf of invoice.performances) {
        lines.push({
            playName: plays[perf.playID].name,
            amount: calcThisAmount(plays[perf.playID], perf) / 100,
            audience: perf.audience
        });
    }
    return lines;
}

function calculateInvoiceData(invoice, plays) {
    let invoiceData = {
        customer: invoice.customer,
        lines: calculateLines(invoice, plays),
        totalAmount: calculateTotalAmount(invoice, plays) / 100,
        volumeCredits: calculateVolumeCredits(invoice, plays)
    };
    return invoiceData;
}

function statement (invoice, plays) {
    let invoiceData = calculateInvoiceData(invoice, plays);
    return print(invoiceData, plainTextPrinter);
}

function print(invoiceData, printer) {
    return printer(invoiceData);
}

function htmlPrinter(invoiceData) {
    const format = new Intl.NumberFormat("en-US",
        {
            style: "currency", currency: "USD",
            minimumFractionDigits: 2
        }).format;

    let result = `<h1>Statement for ${invoiceData.customer}</h1>\n`;

    for (const lineData of invoiceData.lines) {
        result += printLine(lineData, format);
    }

    result += `Amount owed is ${format(invoiceData.totalAmount)}\n`;
    result += `You earned ${invoiceData.volumeCredits} credits\n`;
    return result;
}


function plainTextPrinter(invoiceData) {
    const format = new Intl.NumberFormat("en-US",
        {
            style: "currency", currency: "USD",
            minimumFractionDigits: 2
        }).format;

    let result = `Statement for ${invoiceData.customer}\n`;

    for (const lineData of invoiceData.lines) {
        result += printLine(lineData, format);
    }

    result += `Amount owed is ${format(invoiceData.totalAmount)}\n`;
    result += `You earned ${invoiceData.volumeCredits} credits\n`;
    return result;
}

function printLine(lineData, format) {
    return ` ${lineData.playName}: ${format(lineData.amount)} (${lineData.audience} seats)\n`;
}

module.exports = statement;
