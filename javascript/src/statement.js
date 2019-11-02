function calcTotalAmount(invoice, plays) {
    let totalAmount = 0;
    for (let perf of invoice.performances) {
        totalAmount += calcThisAmount(plays[perf.playID], perf);
    }
    return totalAmount;
}

function calcVolumeCredits(perf, plays) {
    volumeCredits = 0;
    volumeCredits += Math.max(perf.audience - 30, 0);
    if ("comedy" === plays[perf.playID].type) volumeCredits += Math.floor(perf.audience / 5);
    return volumeCredits;
}

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

function calcTotalVolumeCredits(invoice, plays) {
    let volumeCredits = 0;
    for (let perf of invoice.performances) {
        // add volume credits
        volumeCredits += calcVolumeCredits(perf, plays);
    }
    return volumeCredits;
}

function calcLines(invoice, plays) {
    let lines = [];
    for (let perf of invoice.performances) {
        lines.push({
            name: plays[perf.playID].name,
            amount: calcThisAmount(plays[perf.playID], perf),
            audience: perf.audience
        });
    }
    return lines;
}

function calcInvoiceData(invoice, plays) {
    return {
        customer: invoice.customer,
        lines: calcLines(invoice, plays),
        totalAmount: calcTotalAmount(invoice, plays),
        volumeCredits: calcTotalVolumeCredits(invoice, plays)
    };
}

function statement (invoice, plays) {
    let invoiceData = calcInvoiceData(invoice, plays);
    return print(invoiceData, plainTextPrinter);
}

function print(invoiceData, plainTextPrinter) {
    return plainTextPrinter(invoiceData);
}

function plainTextPrinter(invoiceData) {
    const format = new Intl.NumberFormat("en-US",
        {
            style: "currency", currency: "USD",
            minimumFractionDigits: 2
        }).format;
    let result = `Statement for ${invoiceData.customer}\n`;
    for (const line of invoiceData.lines) {
        result += printLine(line, format);
    }
    result += `Amount owed is ${format(invoiceData.totalAmount / 100)}\n`;
    result += `You earned ${invoiceData.volumeCredits} credits\n`;
    return result;
}

function printLine(lineData, format) {
    return ` ${lineData.name}: ${format(lineData.amount / 100)} (${lineData.audience} seats)\n`;
}

module.exports = statement;
