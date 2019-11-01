function calculateInvoiceData(invoice, plays) {
    function calcAmount(play, perf) {
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

    function calcLineData(invoice, plays) {
        let lineDatas = [];
        for (let perf of invoice.performances) {
            lineDatas.push({
                name: plays[perf.playID].name,
                totalAmount: calcAmount(plays[perf.playID], perf),
                audience: perf.audience
            });
        }
        return lineDatas;
    }

    function calcTotalVolumeCredits(invoice, plays) {
        let volumeCredits = 0;
        for (let perf of invoice.performances) {
            volumeCredits += calcVolumeCredits(perf, plays);
        }
        return volumeCredits;
    }

    function calcVolumeCredits(perf, plays) {
        let thisVolumeCredits = Math.max(perf.audience - 30, 0);
        if ("comedy" === plays[perf.playID].type) thisVolumeCredits += Math.floor(perf.audience / 5);
        return thisVolumeCredits;
    }

    function calcTotalAmount(invoice, plays) {
        let totalAmount = 0;
        for (let perf of invoice.performances) {
            totalAmount += calcAmount(plays[perf.playID], perf);
        }
        return totalAmount;
    }

    return {
        customer: invoice.customer,
        lines: calcLineData(invoice, plays),
        totalAmount: calcTotalAmount(invoice, plays),
        volumeCredits: calcTotalVolumeCredits(invoice, plays)
    };
}

function statement(invoice, plays) {
    const invoiceData = calculateInvoiceData(invoice, plays);
    return print(invoiceData, plainTextPrinter);
}

function plainTextPrinter(invoiceData) {
    let result = `Statement for ${invoiceData.customer}\n`;
    for (const lineData of invoiceData.lines) {
        result += printLine(lineData, usFormat());
    }
    result += `Amount owed is ${usFormat()(invoiceData.totalAmount / 100)}\n`;
    result += `You earned ${invoiceData.volumeCredits} credits\n`;
    return result;
}

function print(invoiceData, printer) {
    return printer(invoiceData);
}

function printLine(lineData, format) {
    return ` ${lineData.name}: ${format(lineData.totalAmount / 100)} (${lineData.audience} seats)\n`;
}

function usFormat() {
    return new Intl.NumberFormat("en-US",
        {
            style: "currency", currency: "USD",
            minimumFractionDigits: 2
        }).format;
}

module.exports = statement;
